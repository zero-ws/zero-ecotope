package io.zerows.plugins.trash;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HConfig;

/*
 * Trash Client for
 * 1) backup
 * 2) restore
 */
public interface TrashClient {

    static TrashClient createClient(final Vertx vertx, final HConfig config) {
        return new TrashClientImpl(vertx, config);
    }

    /*
     * Backup record for each deleting
     */
    Future<JsonObject> backupAsync(String identifier, JsonObject record, MultiMap params);

    Future<JsonObject> backupAsync(String identifier, JsonObject record);

    Future<JsonArray> backupAsync(String identifier, JsonArray records, MultiMap params);

    Future<JsonArray> backupAsync(String identifier, JsonArray records);

    /*
     * Restore record for each deleting
     */
    Future<JsonObject> restoreAsync(String identifier, JsonObject record, MultiMap params);
}
