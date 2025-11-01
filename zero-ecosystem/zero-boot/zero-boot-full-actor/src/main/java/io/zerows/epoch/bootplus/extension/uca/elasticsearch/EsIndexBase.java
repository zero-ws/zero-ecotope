package io.zerows.epoch.bootplus.extension.uca.elasticsearch;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.boot.extension.util.Ox;
import io.zerows.component.log.LogOf;
import io.zerows.plugins.store.elasticsearch.ElasticSearchClient;
import io.zerows.plugins.store.elasticsearch.ElasticSearchInfix;

import java.util.function.Supplier;

abstract class EsIndexBase implements EsIndex {

    protected final transient String identifier;
    protected final transient ElasticSearchClient client;

    public EsIndexBase(final String identifier) {
        this.identifier = identifier;
        this.client = ElasticSearchInfix.getClient();
    }

    protected <T> Future<T> runSingle(final T input, final Supplier<T> supplier) {
        return Ox.runSafe(this.getClass(), input, supplier);
    }

    protected Future<JsonArray> runBatch(final JsonArray input, final Supplier<Boolean> supplier) {
        return Ox.runSafe(this.getClass(), input, () -> {
            final Boolean batched = supplier.get();
            this.logger().info("EsIndex result = {0}", batched);
            return input;
        });
    }

    protected LogOf logger() {
        return LogOf.get(this.getClass());
    }
}
