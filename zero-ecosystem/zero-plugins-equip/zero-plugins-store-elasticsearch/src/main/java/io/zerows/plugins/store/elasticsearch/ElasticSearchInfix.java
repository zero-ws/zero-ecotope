package io.zerows.plugins.store.elasticsearch;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.epoch.sdk.plugins.Infix;

/**
 * @author Hongwei
 * @since 2019/12/28, 16:09
 */

@Infusion
public class ElasticSearchInfix implements Infix {
    private static final String NAME = "ZERO_ELASTIC_SEARCH_POOL";

    private static final Cc<String, ElasticSearchClient> CC_CLIENT = Cc.open();

    private static void initInternal(final Vertx vertx) {
        CC_CLIENT.pick(() -> Infix.init(YmlCore.inject.ES,
            (config) -> ElasticSearchClient.createShared(vertx, config),
            ElasticSearchInfix.class), NAME);
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx);
    }

    public static ElasticSearchClient getClient() {
        return CC_CLIENT.get(NAME);
    }

    @Override
    @SuppressWarnings("all")
    public ElasticSearchClient get() {
        return getClient();
    }
}
