package io.zerows.sdk.plugins;

import io.vertx.core.json.JsonObject;

/**
 * Uniform third part interface for client
 */
@Deprecated
public interface InfixClient<T> {

    T init(JsonObject params);
}
