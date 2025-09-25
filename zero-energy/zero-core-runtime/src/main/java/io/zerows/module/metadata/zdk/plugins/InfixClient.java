package io.zerows.module.metadata.zdk.plugins;

import io.vertx.core.json.JsonObject;

/**
 * Uniform third part interface for client
 */
public interface InfixClient<T> {

    T init(JsonObject params);
}
