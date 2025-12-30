package io.zerows.plugins.session;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.epoch.annotations.Defer;
import io.zerows.epoch.basicore.YmSpec;
import io.zerows.specification.configuration.HConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@Defer
class SessionClientImpl implements SessionClient {

    private static final SessionManager MANAGER = SessionManager.of();
    private final SessionStore store;
    private final HConfig config;

    private SessionClientImpl(final Vertx vertx, final HConfig config) {
        this.config = config;
        final String key = SessionUtil.keyOf(vertx, config);
        this.store = MANAGER.STORE().get(key, (ignored) -> SessionUtil.createStore(vertx, config));
    }

    static SessionClientImpl create(final Vertx vertx, final HConfig config) {
        return new SessionClientImpl(vertx, config);
    }

    @Override
    public Future<Session> getAsync(final String id) {
        final Promise<Session> promise = Promise.promise();
        this.store.get(id).onComplete(result -> {
            if (result.succeeded()) {
                promise.complete(result.result());
            } else {
                promise.complete(null);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Session> createAsync(final String sessionId) {
        final Integer timeout = this.config.options(YmSpec.vertx.session.timeout, 120);
        final long ms = TimeUnit.MINUTES.toMillis(timeout);
        return Future.succeededFuture(this.store.createSession(ms));
    }
}
