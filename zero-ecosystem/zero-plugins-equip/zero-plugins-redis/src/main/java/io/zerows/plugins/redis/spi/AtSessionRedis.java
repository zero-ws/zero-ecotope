package io.zerows.plugins.redis.spi;

import io.r2mo.typed.annotation.SPID;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.SessionStore;
import io.zerows.cortex.sdk.AtSession;

/**
 * @author lang : 2025-12-31
 */
@SPID("AtSession/SPI")
public class AtSessionRedis implements AtSession {
    @Override
    public Future<SessionStore> createStore(final Vertx vertx, final JsonObject session) {
        return null;
    }
}
