package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface LogWriter {
    Future<JsonObject> write(LogType type, JsonObject data);
}
