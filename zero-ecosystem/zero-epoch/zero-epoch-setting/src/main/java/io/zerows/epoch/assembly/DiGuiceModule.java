package io.zerows.epoch.assembly;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;
import io.zerows.epoch.annotations.Defer;
import io.zerows.support.Ut;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

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

    @SuppressWarnings("all")
    protected <T extends I, I> Set<String> bindInterface(final Class<I> interfaceCls, final Set<Class<T>> implSet) {
        // ❌️ 无法查找到实现类，跳过
        if (implSet.isEmpty()) {
            return null;
        }

        Set<String> clazzSet = new HashSet<>();
        // AddOn 先执行
        clazzSet = this.bindInterface(implSet, (implCls, name) -> this.bindAddOn(interfaceCls, implCls, name));
        if (!clazzSet.isEmpty()) {
            return clazzSet;
        }

        clazzSet = this.bindInterface(implSet, (implCls, name) -> this.bindDefault(interfaceCls, implCls, name));
        return clazzSet;
    }

    private <T extends I, I> Set<String> bindInterface(final Set<Class<T>> implSet, final BiFunction<Class<T>, String, Class<T>> consumerFn) {
        // 记录被注入的记录
        final Set<String> clazzSet = new HashSet<>();
        if (1 == implSet.size()) {
            final Class<T> clazz = implSet.iterator().next();
            final Class<T> bind = consumerFn.apply(clazz, null);
            // 追加记录
            if (Objects.isNull(bind)) {
                clazzSet.add(clazz.getName());
            }
        } else {
            // 多实现类必须使用 @Named 注解
            implSet.forEach(implCls -> {
                if (implCls.isAnnotationPresent(Named.class)) {
                    final Named annotation = implCls.getAnnotation(Named.class);
                    final String name = annotation.value();
                    final Class<T> bind = consumerFn.apply(implCls, name);
                    if (Objects.isNull(bind)) {
                        // 追加记录
                        clazzSet.add(implCls.getName());
                    }
                }
            });
        }
        return clazzSet;
    }

    private <T extends I, I> Class<T> bindAddOn(final Class<I> interfaceCls, final Class<T> implCls, final String name) {
        if (!implCls.isAnnotationPresent(Defer.class)) {
            // ❌️ 动态注册表未标记
            return null;
        }

        if (Ut.isNil(name)) {
            this.bind(interfaceCls).toProvider(new DiDynamicProvider<>(Key.get(interfaceCls)));
            log.info("[ ZERO ] ( DI ) Defer / 实现类: `{}`, 接口 = `{}`", implCls.getName(), interfaceCls.getName());
        } else {
            this.bind(interfaceCls).annotatedWith(Names.named(name)).toProvider(
                // 注意此处是带有 @Named 的操作，在实现这一层需要去考虑
                new DiDynamicProvider<>(Key.get(interfaceCls, Names.named(name)))
            );
            log.info("[ ZERO ] ( DI ) Defer / 实现类: `{}`, 接口 = `{}`, 标识 = {}",
                implCls.getName(), interfaceCls.getName(), name);
        }
        return implCls;
    }

    private <T extends I, I> Class<T> bindDefault(final Class<I> interfaceCls, final Class<T> implCls, final String name) {
        if (implCls.isAnnotationPresent(Defer.class)) {
            // ❌️ 动态注册表模式的注入
            return null;
        }
        if (Ut.isNil(name)) {
            this.buildInstance(implCls,
                this.bind(interfaceCls).to(implCls));
            log.info("[ ZERO ] ( DI ) 实现类: `{}`, 接口 = `{}`", implCls.getName(), interfaceCls.getName());
        } else {
            this.buildInstance(implCls,
                this.bind(interfaceCls).annotatedWith(Names.named(name)).to(implCls));
            log.info("[ ZERO ] ( DI ) 实现类: `{}`, 接口 = `{}`, 标识 = {}",
                implCls.getName(), interfaceCls.getName(), name);
        }
        return implCls;
    }

    private <T> void buildInstance(final Class<T> implCls, final ScopedBindingBuilder scopedBinding) {
        if (implCls.isAnnotationPresent(Singleton.class)) {
            scopedBinding.asEagerSingleton();
        }
    }
}
