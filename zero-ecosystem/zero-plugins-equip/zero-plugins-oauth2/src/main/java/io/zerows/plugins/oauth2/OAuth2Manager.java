package io.zerows.plugins.oauth2;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.plugins.oauth2.metadata.OAuth2Config;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

class OAuth2Manager {

    private static final OAuth2Manager INSTANCE = new OAuth2Manager();
    private static final Cc<Integer, OAuth2Config> CC_CONFIG = Cc.open();
    private static final Cc<Integer, OAuth2Security> CC_SECURITY = Cc.open();
    private static final Cc<Integer, AtomicReference<JsonObject>> CC_KEYSTORE = Cc.open();

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

    void securityOf(final Vertx vertx, final JsonObject securityJ) {
        if (Ut.isNil(securityJ)) {
            return;
        }

        CC_SECURITY.pick(
            () -> Ut.deserialize(securityJ, OAuth2Security.class),
            System.identityHashCode(vertx)
        );
    }

    OAuth2Security securityOf(final Vertx vertx) {
        if (Objects.isNull(vertx)) {
            return null;
        }
        return CC_SECURITY.getOrDefault(System.identityHashCode(vertx), null);
    }

    OAuth2Security securityOf() {
        final Vertx vertx = StoreVertx.of().vertx();
        return this.securityOf(vertx);
    }

    void keystoreOf(final Vertx vertx, final JsonObject keystoreJ) {
        if (Objects.isNull(keystoreJ)) {
            return;
        }
        CC_KEYSTORE.pick(
            () -> new AtomicReference<>(keystoreJ),
            System.identityHashCode(vertx)
        );
    }

    JsonObject keystoreOf(final Vertx vertx) {
        if (Objects.isNull(vertx)) {
            return null;
        }
        final AtomicReference<JsonObject> ref = CC_KEYSTORE.getOrDefault(System.identityHashCode(vertx), null);
        if (Objects.isNull(ref)) {
            return null;
        }
        return ref.get();
    }

    JsonObject keystoreOf() {
        final Vertx vertx = StoreVertx.of().vertx();
        return this.keystoreOf(vertx);
    }
}
