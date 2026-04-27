package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface ExLog {
    Future<JsonObject> record(LogType type, JsonObject data);

    default Future<JsonObject> system(final JsonObject data) {
        return this.record(LogType.SYSTEM, data);
    }
}
