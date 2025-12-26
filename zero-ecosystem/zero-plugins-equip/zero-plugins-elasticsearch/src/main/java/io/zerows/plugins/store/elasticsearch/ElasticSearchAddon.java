package io.zerows.plugins.store.elasticsearch;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.sdk.plugins.AddOnManager;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

public class ElasticSearchAddon extends AddOnBase<ElasticSearchClient> {
    private static ElasticSearchAddon INSTANCE;

    protected ElasticSearchAddon(Vertx vertx, HConfig config) {
        super(vertx, config);
    }

    public static AddOn<ElasticSearchClient> of(Vertx vertx, HConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new ElasticSearchAddon(vertx, config);
        }
        return INSTANCE;
    }

    public static AddOn<ElasticSearchClient> of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected AddOnManager<ElasticSearchClient> manager() {
        return ElasticSearchManager.of();
    }

    @Override
    protected ElasticSearchClient createInstanceBy(String name) {
        return ElasticSearchClient.createShared(this.vertx(),this.config());
    }
}