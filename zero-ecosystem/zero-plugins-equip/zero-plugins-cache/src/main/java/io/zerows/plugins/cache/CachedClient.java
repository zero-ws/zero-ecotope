package io.zerows.plugins.cache;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOn;

/**
 * @author lang : 2026-01-02
 */
@AddOn.Name("DEFAULT_MEM_CLIENT")
public interface CachedClient extends BaseClient {

    static CachedClient createClient(final Vertx vertx, final JsonObject options) {
        return CachedClientImpl.create(vertx, options);
    }
}
