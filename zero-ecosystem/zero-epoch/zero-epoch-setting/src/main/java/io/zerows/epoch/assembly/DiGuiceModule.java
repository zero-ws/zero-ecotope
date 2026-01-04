package io.zerows.epoch.assembly;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;
import io.zerows.epoch.annotations.Defer;
import io.zerows.support.Ut;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
public abstract class DiGuiceModule extends AbstractModule {

    protected <T> String bindConstructor(final Class<T> clazz) {
        // ❌️ 如果没有唯一参数的构造函数，则跳过
        if (!ExtractTool.isDefaultConstructor(clazz)) {
            return clazz.getName();
        }

        final Constructor<T> constructor = Ut.constructor(clazz);
        if (clazz.isAnnotationPresent(Singleton.class)) {
            // 追加了 @Singleton 注解
            this.bind(clazz).toConstructor(constructor).asEagerSingleton();
        } else {
            // 未追加 @Singleton 注解
            this.bind(clazz).toConstructor(constructor);
        }
        log.info("[ ZERO ] ( DI ) 构造函数: `{}`", clazz);
        return null;
    }

    protected <T extends I, I> Set<String> bindInterface(final Class<I> interfaceCls, final Set<Class<T>> implSet) {
        // ❌️ 无法查找到实现类，跳过
        if (implSet.isEmpty()) {
            return null;
        }

        final boolean isSingleImpl = (1 == implSet.size());

        // =========================================================
        // 阶段 1：并行计算 (Parallel Computing)
        // 利用 parallelStream 进行反射分析和日志拼接。
        // Java ForkJoinPool 会自动评估开销：任务少时原地执行(无切换)，任务多时并行执行。
        // 这是理论上开销最小、速度最快的方式。
        // =========================================================
        final List<BindingTask> tasks = implSet.parallelStream()
            .map(implCls -> this.analyze(interfaceCls, implCls, isSingleImpl))
            .filter(Objects::nonNull)
            .toList();

        // =========================================================
        // 阶段 2：串行注册 (Serial Binding)
        // Guice Binder 不是线程安全的，必须在主线程串行操作
        // =========================================================
        final Set<String> clazzSet = new HashSet<>();

        for (final BindingTask task : tasks) {
            if (task.skipped) {
                // 对应原逻辑中返回 null 的情况
                clazzSet.add(task.implCls.getName());
                continue;
            }

            // 执行核心绑定逻辑
            this.applyBinding(task);

            // 输出预计算好的日志
            log.info(task.logMessage);
        }

        return clazzSet;
    }

    // ----------------------------------------------------------------------
    // 私有分析方法：运行在并行线程中 (纯计算，无副作用)
    // ----------------------------------------------------------------------
    private <T extends I, I> BindingTask analyze(final Class<I> interfaceCls, final Class<T> implCls, final boolean isSingleImpl) {

        final boolean isDefer = implCls.isAnnotationPresent(Defer.class);
        String name = null;
        boolean isValid = false;

        if (isSingleImpl) {
            // 单实现类模式，无需 Named
            isValid = true;
        } else {
            // 多实现类模式，必须有 Named
            if (implCls.isAnnotationPresent(Named.class)) {
                final Named annotation = implCls.getAnnotation(Named.class);
                name = annotation.value();
                isValid = true;
            }
        }

        if (!isValid) {
            // 不满足绑定条件，标记跳过
            return BindingTask.builder().implCls(implCls).skipped(true).build();
        }

        // --- 生成日志字符串 (回归最原始的清爽格式) ---
        final String logMsg;
        if (isDefer) {
            // AddOn / Defer 逻辑
            if (Ut.isNil(name)) {
                logMsg = Ut.fromMessage("[ ZERO ] ( DI ) Defer / 实现类: `{}`, 接口 = `{}`",
                    implCls.getName(), interfaceCls.getName());
            } else {
                logMsg = Ut.fromMessage("[ ZERO ] ( DI ) Defer / 实现类: `{}`, 接口 = `{}`, 标识 = {}",
                    implCls.getName(), interfaceCls.getName(), name);
            }
        } else {
            // Default 逻辑
            if (Ut.isNil(name)) {
                logMsg = Ut.fromMessage("[ ZERO ] ( DI ) 实现类: `{}`, 接口 = `{}`",
                    implCls.getName(), interfaceCls.getName());
            } else {
                logMsg = Ut.fromMessage("[ ZERO ] ( DI ) 实现类: `{}`, 接口 = `{}`, 标识 = {}",
                    implCls.getName(), interfaceCls.getName(), name);
            }
        }

        return BindingTask.builder()
            .interfaceCls(interfaceCls)
            .implCls(implCls)
            .name(name)
            .isDefer(isDefer)
            .isSingleton(implCls.isAnnotationPresent(Singleton.class))
            .logMessage(logMsg)
            .skipped(false)
            .build();
    }

    // ----------------------------------------------------------------------
    // 执行绑定：解决泛型编译问题 (运行在主线程)
    // ----------------------------------------------------------------------
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void applyBinding(final BindingTask task) {
        // 强制转型为 Class<Object>，解决编译器泛型检查报错
        final Class<Object> iface = (Class<Object>) task.interfaceCls;
        final Class<Object> impl = (Class<Object>) task.implCls;

        if (task.isDefer) {
            // Defer -> toProvider
            if (Ut.isNil(task.name)) {
                this.bind(iface).toProvider(new DiDynamicProvider(Key.get(iface)));
            } else {
                this.bind(iface).annotatedWith(Names.named(task.name)).toProvider(
                    new DiDynamicProvider(Key.get(iface, Names.named(task.name)))
                );
            }
        } else {
            // Default -> to
            final ScopedBindingBuilder bindingBuilder;
            if (Ut.isNil(task.name)) {
                bindingBuilder = this.bind(iface).to(impl);
            } else {
                bindingBuilder = this.bind(iface).annotatedWith(Names.named(task.name)).to(impl);
            }

            // 处理 Singleton
            if (task.isSingleton) {
                bindingBuilder.asEagerSingleton();
            }
        }
    }

    // --- 内部 DTO：用于存储并行计算的结果 ---
    @Data
    @Builder
    private static class BindingTask {
        Class<?> interfaceCls;
        Class<?> implCls;
        String name;           // @Named 的值
        boolean isDefer;       // 是否是 Defer 模式
        boolean isSingleton;   // 是否是单例
        String logMessage;     // 预计算的日志
        boolean skipped;       // 是否被逻辑跳过
    }
}