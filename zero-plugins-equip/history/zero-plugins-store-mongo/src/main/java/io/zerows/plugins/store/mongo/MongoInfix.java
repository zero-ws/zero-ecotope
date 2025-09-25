package io.zerows.plugins.store.mongo;

import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;
import io.zerows.core.annotations.Infusion;
import io.zerows.core.constant.configure.YmlCore;
import io.r2mo.typed.cc.Cc;
import io.zerows.module.metadata.zdk.plugins.Infix;

/**
 *
 */
@Infusion
@SuppressWarnings("unchecked")
public class MongoInfix implements Infix {

    private static final String NAME = "ZERO_MONGO_POOL";
    /**
     * All Configs
     **/
    private static final Cc<String, MongoClient> CC_CLIENT = Cc.open();

    private static void initInternal(final Vertx vertx,
                                     final String name) {
        CC_CLIENT.pick(() -> Infix.init(YmlCore.inject.MONGO,
            (config) -> MongoClient.createShared(vertx, config, name),
            MongoInfix.class), name);
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx, NAME);
    }

    public static MongoClient getClient() {
        return CC_CLIENT.store(NAME);
    }

    @Override
    public MongoClient get() {
        return getClient();
    }
}
