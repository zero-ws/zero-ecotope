package io.zerows.plugins.session;

import io.vertx.core.Vertx;
import io.zerows.sdk.plugins.AddOnBase;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

/**
 * @author lang : 2025-10-14
 */
class SessionAddOn extends AddOnBase<SessionClient> {

    private static SessionAddOn INSTANCE;

    private SessionAddOn(final Vertx vertx, final HConfig config) {
        super(vertx, config);
    }

    static SessionAddOn of(final Vertx vertx, final HConfig config) {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new SessionAddOn(vertx, config);
        }
        return INSTANCE;
    }

    static SessionAddOn of() {
        return Objects.requireNonNull(INSTANCE);
    }

    @Override
    protected SessionManager manager() {
        return SessionManager.of();
    }

    @Override
    protected SessionClient createInstanceBy(final String name) {
        return SessionClient.createClient(this.vertx(), this.config());
    }
}
