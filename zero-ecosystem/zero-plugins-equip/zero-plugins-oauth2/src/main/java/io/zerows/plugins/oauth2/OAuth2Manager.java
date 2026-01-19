package io.zerows.plugins.oauth2;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.plugins.oauth2.metadata.OAuth2Config;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Objects;

class OAuth2Manager {

    private static final OAuth2Manager INSTANCE = new OAuth2Manager();
    private static final Cc<Integer, OAuth2Config> CC_CONFIG = Cc.open();

    static OAuth2Manager of() {
        return INSTANCE;
    }

    void configOf(final Vertx vertx, final HConfig config) {
        CC_CONFIG.pick(
            () -> Ut.deserialize(config.options(), OAuth2Config.class),
            System.identityHashCode(vertx)
        );
    }

    OAuth2Config configOf(final Vertx vertx) {
        if (Objects.isNull(vertx)) {
            return null;
        }
        return CC_CONFIG.getOrDefault(System.identityHashCode(vertx), null);
    }

    OAuth2Config configOf() {
        final Vertx vertx = StoreVertx.of().vertx();
        return this.configOf(vertx);
    }
}
