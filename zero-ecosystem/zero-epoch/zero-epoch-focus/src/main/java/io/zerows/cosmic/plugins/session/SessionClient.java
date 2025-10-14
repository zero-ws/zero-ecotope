package io.zerows.cosmic.plugins.session;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Session;
import io.zerows.specification.configuration.HConfig;

/*
 * Session Client in zero system, it could be enabled by zero
 * and keep session when authorization.
 *
 * Keep only one session get.
 */
public interface SessionClient {
    /*
     * Create local session get bind data
     */
    static SessionClient createClient(final Vertx vertx, final HConfig config) {
        return SessionClientImpl.create(vertx, config);
    }

    /*
     * Get Session by id
     */
    Future<Session> getAsync(String id);

    /*
     * Open new Session
     */
    Future<Session> createAsync(String id);
}
