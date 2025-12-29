package io.zerows.plugins.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Set;
import java.util.function.Supplier;

/**
 * 对应配置片段
 * <pre>
 *     monitor:
 *       clients:
 *       - name: ???
 *         component: ???
 *         enabled: true/false
 *       roles:
 *       - id: ???
 *         component: ???
 *         configYaml: {@link JsonObject}
 * </pre>
 *
 * @author lang : 2025-12-29
 */
public interface QuotaData {

    Cc<String, Supervisor<?, ?>> CC_SUPERVISOR = Cc.openThread();

    @SuppressWarnings("unchecked")
    static <K, V> Supervisor<K, V> mom(final Supplier<Supervisor<K, V>> constructorFn) {
        return (Supervisor<K, V>) CC_SUPERVISOR.pick(constructorFn::get, String.valueOf(constructorFn.get()));
    }

    Future<Boolean> register(JsonObject config, MeterRegistry registry, Vertx vertxRef);

    /**
     * 后台监控专用接口，一般是读取
     * <pre>
     *     1. 键读取，外层迭代
     *     2. 值读取，内层获取
     * </pre>
     *
     * @param <K> 键类型
     * @param <V> 值类型
     */
    interface Supervisor<K, V> {

        Set<K> keys();

        V value(K key);
    }
}
