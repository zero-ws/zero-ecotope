package io.zerows.epoch.corpus.web.cache.shared;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.epoch.sdk.plugins.Infix;

@Infusion
@SuppressWarnings("all")
public class MapInfix implements Infix {

    private static final String NAME = "ZERO_MAP_POOL";
    private static final Cc<String, SharedClient<String, Object>> CC_CLIENTS = Cc.open();

    private static void initInternal(final Vertx vertx,
                                     final String name) {
        CC_CLIENTS.pick(() -> SharedClient.createShared(vertx, name), name);
        // FnZero.po?l(CLIENTS, name, () -> SharedClient.createShared(vertx, name));
    }

    public static void init(final Vertx vertx) {
        initInternal(vertx, NAME);
    }

    public static SharedClient<String, Object> getClient() {
        return CC_CLIENTS.get(NAME);
    }

    public static String getDefaultName() {
        return NAME;
    }

    @Override
    public SharedClient<String, Object> get() {
        return getClient();
    }
}
