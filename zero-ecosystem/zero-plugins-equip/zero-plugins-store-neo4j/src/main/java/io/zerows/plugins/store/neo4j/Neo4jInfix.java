package io.zerows.plugins.store.neo4j;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.sdk.plugins.Infix;

@Infusion
@SuppressWarnings("all")
public class Neo4jInfix implements Infix {

    private static final String NAME = "ZERO_NEO4J_POOL";
    private static final Cc<String, Neo4jClient> CC_CLIENT = Cc.open();

    private static void initInternal(final Vertx vertx, final String name) {
        CC_CLIENT.pick(() -> Infix.init(YmlCore.inject.NEO4J,
            config -> Neo4jClient.createShared(vertx, config),
            Neo4jInfix.class), name);
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx, NAME);
    }

    public static Neo4jClient getClient() {
        return CC_CLIENT.get(NAME);
    }

    @Override
    public Neo4jClient get() {
        return getClient();
    }

}
