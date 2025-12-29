package io.zerows.plugins.monitor.metadata;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;

import java.util.function.Supplier;

/**
 * @author lang : 2025-12-29
 */
public interface MetricParser<M extends MetricBase> {
    Cc<String, MetricParser<?>> CC_PARSER = Cc.openThread();

    @SuppressWarnings("unchecked")
    static <M extends MetricBase> MetricParser<M> of(final Supplier<MetricParser<M>> constructorFn) {
        return (MetricParser<M>) CC_PARSER.pick(constructorFn::get, String.valueOf(constructorFn.hashCode()));
    }

    M build(JsonObject config);
}
