package io.zerows.epoch.configuration;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.atomic.HPlug;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

/**
 * 插件专用配置项，其数据格式如
 * <pre><code>
 *     plugins:
 *       component: {options}
 *       component: {options}
 * </code></pre>
 *
 * @author lang : 2024-06-27
 */
public class ZeroPlugins {
    private static final Cc<String, HPlug> CC_PLUGIN = Cc.openThread();
    private static final Cc<String, ZeroPlugins> CC_SKELETON = Cc.open();
    private final Vertx vertxRef;

    private ZeroPlugins(final Vertx vertxRef) {
        this.vertxRef = vertxRef;
    }

    public static ZeroPlugins of(final Vertx vertxRef) {
        final String cacheKey = "VERTX@" + vertxRef.hashCode() + ZeroPlugins.class.getName();
        return CC_SKELETON.pick(() -> new ZeroPlugins(vertxRef), cacheKey);
    }

    @SuppressWarnings("unchecked")
    public <T extends HPlug> T createPlugin(final Class<?> interfaceCls) {
        final HConfig config = NodeStore.findPlugin(this.vertxRef, interfaceCls);
        if (Objects.isNull(config)) {
            return null;
        }
        final Class<?> implCls = config.executor();
        final JsonObject options = config.options();
        final String cacheKey = "VERTX@" + this.vertxRef.hashCode()     // 实例维度
            + "/" + implCls.getName()                                   // 执行维度
            + options.hashCode();                                       // 配置维度
        return (T) CC_PLUGIN.pick(() -> {
            final T instance = SourceReflect.instance(implCls);
            return instance.bind(options);
        }, cacheKey);
    }
}
