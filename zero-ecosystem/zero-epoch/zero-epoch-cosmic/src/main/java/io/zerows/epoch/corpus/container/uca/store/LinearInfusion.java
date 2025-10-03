package io.zerows.epoch.corpus.container.uca.store;

import io.r2mo.function.Fn;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.constant.KPlugin;
import io.zerows.epoch.corpus.container.exception._40016Exception500PluginInitialize;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.management.OZeroStore;
import io.zerows.sdk.osgi.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 不支持 start / stop，Infix 架构为特殊架构，直接在 initialize 方法中执行每个 Infix 对应的初始化方法来完成执行操作，且这种操作只有
 * 针对非 OSGI 环境有效，若 OSGI 环境则要走另外的 Infix 服务来实现初始化，插件模式
 *
 * @author lang : 2024-05-03
 */
class LinearInfusion extends AbstractAmbiguity implements StubLinear {
    LinearInfusion(final Bundle bundle) {
        super(bundle);
    }

    /**
     * 扫描所有 Infusion，Infix 架构专用的接入类信息，Infix接入类基本规范
     * <pre><code>
     *     1. 此类必须是带有 {@link Infusion} 注解的类型
     *     2. 此类必须包含初始化静态方法（同步方法）
     * </code></pre>
     *
     * @param classSet 可用注解类
     * @param runVertx 运行实例
     */
    @Override
    public void initialize(final Set<Class<?>> classSet, final RunVertx runVertx) {
        // Native & Standard:
        //   读取所有的 Infusion 类
        final ConcurrentMap<String, Class<?>> injectionCls = OZeroStore.classInject();
        injectionCls.values().stream()
            .filter(Objects::nonNull)
            .filter(item -> item.isAnnotationPresent(Infusion.class))
            .forEach(item -> this.findAndInit(item, runVertx.instance()));

        // Extension:
        //   扫描所有扩展的 Infusion 类
        injectionCls.keySet().stream()
            .filter(key -> !KPlugin.INJECT_STANDARD.contains(key)).map(injectionCls::get)
            .filter(Objects::nonNull)
            .filter(item -> item.isAnnotationPresent(Infusion.class))
            .forEach(item -> this.findAndInit(item, runVertx.instance()));
    }

    /**
     * 初始化 {@link Infusion} 主类，此主类必须带有签名
     * <pre><code>
     *     public static void init(final Vertx vertx);
     *     1）静态方法
     *     2）返回值为 void
     *     3）单参 {@link Vertx}
     * </code></pre>
     *
     * @param clazz 类名
     */
    private void findAndInit(final Class<?> clazz, final Vertx vertxRef) {
        final Method methodInit = this.findMethod(clazz);
        if (Objects.isNull(methodInit)) {
            throw new _40016Exception500PluginInitialize(clazz.getName());
        }
        Fn.jvmAt(() -> methodInit.invoke(null, vertxRef));
    }

    private Method findMethod(final Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        return Arrays.stream(methods).filter(item -> {
            if (!"init".equals(item.getName())) {
                // 方法名
                return false;
            }
            if (!(void.class == item.getReturnType() || Void.class == item.getReturnType())) {
                // 返回值
                return false;
            }
            final int modifier = item.getModifiers();
            // public static
            return Modifier.isStatic(modifier) && Modifier.isPublic(modifier);
        }).findAny().orElse(null);
    }
}
