package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface ExTrash {
    /*
     * Backup data to history database here
     */
    Future<JsonObject> backupAsync(String identifier, JsonObject data);

    Future<JsonArray> backupAsync(String identifier, JsonArray data);
}
