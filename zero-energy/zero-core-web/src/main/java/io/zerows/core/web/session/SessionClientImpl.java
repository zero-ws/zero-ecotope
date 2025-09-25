package io.zerows.core.web.session;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.core.web.session.eon.em.SessionType;
import io.zerows.core.web.session.exception._500SessionClientInitException;
import io.zerows.module.metadata.uca.logging.OLog;

import java.util.concurrent.atomic.AtomicBoolean;

public class SessionClientImpl implements SessionClient {

    private static final OLog LOGGER = Ut.Log.plugin(SessionClientImpl.class);
    private static final AtomicBoolean LOG_MSG = new AtomicBoolean(true);
    private static SessionStore STORE;
    private final transient Vertx vertx;

    private SessionClientImpl(final Vertx vertx, final JsonObject config, final SessionType type) {
        this.vertx = vertx;
        if (null == STORE) {
            if (LOG_MSG.getAndSet(Boolean.FALSE)) {
                LOGGER.info(Info.SESSION_MODE, type);
            }
            /* Whether existing get */
            if (SessionType.LOCAL == type) {
                STORE = LocalSessionStore.create(vertx);
            } else if (SessionType.CLUSTER == type) {
                STORE = ClusteredSessionStore.create(this.vertx);
            } else {
                final String store = config.getString(YmlCore.session.config.STORE);
                Fx.outWeb(Ut.isNil(store), _500SessionClientInitException.class, this.getClass());
                LOGGER.info(Info.SESSION_STORE, store);
                /*
                 * SessionStore -> Defined here
                 * The session get could not be singleton because each session get must not
                 * be shared and located by each thread here.
                 */
                final SessionStore defined = Ut.singleton(store);
                JsonObject opts = config.getJsonObject(YmlCore.session.config.OPTIONS);
                if (Ut.isNil(opts)) {
                    opts = new JsonObject();
                }
                STORE = defined.init(vertx, opts);
            }
        }
    }

    static SessionClientImpl create(final Vertx vertx, final JsonObject config) {
        final String type = config.getString(YmlCore.session.config.CATEGORY);
        if (SessionType.CLUSTER.name().equals(type)) {
            /* CLUSTER */
            return new SessionClientImpl(vertx, config, SessionType.CLUSTER);
        } else if (SessionType.DEFINED.name().equals(type)) {
            /* DEFINED */
            return new SessionClientImpl(vertx, config, SessionType.DEFINED);
        } else {
            /* LOCAL ( Default ) */
            return new SessionClientImpl(vertx, config, SessionType.LOCAL);
        }
    }

    @Override
    public SessionHandler getHandler() {
        return SessionHandler.create(STORE)
            /*
             * KRef: https://vertx.io/blog/writing-secure-vert-x-web-apps/
             * */
            // .setCookieSecureFlag(true)
            .setCookieHttpOnlyFlag(true);
    }

    @Override
    public Future<Session> get(final String id) {
        final Promise<Session> promise = Promise.promise();
        STORE.get(id).onComplete(result -> {
            if (result.succeeded()) {
                promise.complete(result.result());
            } else {
                promise.complete(null);
            }
        });
        return promise.future();
    }

    @Override
    public Future<Session> open(final String sessionId) {
        return Future.succeededFuture(STORE.createSession(2000));
    }
}
