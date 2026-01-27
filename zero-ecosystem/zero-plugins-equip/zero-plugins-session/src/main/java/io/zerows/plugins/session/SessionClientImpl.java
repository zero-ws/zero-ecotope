package io.zerows.plugins.session;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.epoch.annotations.Defer;
import io.zerows.epoch.spec.YmSpec;
import io.zerows.specification.configuration.HConfig;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Defer
class SessionClientImpl implements SessionClient {

    private static final SessionManager MANAGER = SessionManager.of();
    private final HConfig config;
    private final Vertx vertxRef;
    @Setter(AccessLevel.PRIVATE)
    private SessionStore store;

    private SessionClientImpl(final Vertx vertx, final HConfig config) {
        this.config = config;
        this.vertxRef = vertx;
    }

    static SessionClientImpl create(final Vertx vertx, final HConfig config) {
        return new SessionClientImpl(vertx, config);
    }

    private Future<SessionStore> store() {
        if (Objects.nonNull(this.store)) {
            return Future.succeededFuture(this.store);
        }

        final String key = SessionUtil.keyOf(this.vertxRef, this.config);
        return MANAGER.getOrCreate(key, () -> SessionUtil.createStore(this.vertxRef, this.config)).compose(created -> {
            this.setStore(created);
            return Future.succeededFuture(created);
        });
    }

    @Override
    public Future<Session> getAsync(final String id) {
        return this.store().compose(store -> store.get(id));
    }

    @Override
    public Future<Session> createAsync(final String sessionId) {
        final Integer timeout = this.config.options(YmSpec.vertx.session.timeout, 120);
        final long ms = TimeUnit.MINUTES.toMillis(timeout);
        return this.store().compose(store -> Future.succeededFuture(store.createSession(ms)));
    }
}
