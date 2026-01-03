package io.zerows.plugins.cache;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOn;

/**
 * Shared client for shared data in vert.x
 */
@AddOn.Name("DEFAULT_SHARED_CLIENT")
public interface SharedClient extends BaseClient {

    static SharedClient createClient(final Vertx vertx, final JsonObject options) {
        return SharedClientImpl.create(vertx, options);
    }
}
