package io.zerows.plugins.trash;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;

import java.util.Objects;

class TrashClientImpl implements TrashClient {
    private final transient Vertx vertxRef;
    private final JsonObject options = new JsonObject();

    public TrashClientImpl(final Vertx vertxRef, final HConfig config) {
        this.vertxRef = vertxRef;
        Objects.requireNonNull(config, "[ PLUG ] Actor 是必须配置的，所以 config 不可能为空！");
        if (Objects.nonNull(config.options())) {
            this.options.mergeIn(config.options(), true);
        }
    }

    @Override
    public Future<JsonObject> backupAsync(final String identifier, final JsonObject record, final MultiMap params) {
        final JsonObject content = Ut.valueJObject(record);
        if (Ut.isNotNil(content)) {
            TrashBuilder.of(identifier, this.options).createHistory(record, params);
        }
        return Future.succeededFuture(record);
    }

    @Override
    public Future<JsonArray> backupAsync(final String identifier, final JsonArray records, final MultiMap params) {
        final JsonArray content = Ut.valueJArray(records);
        if (!content.isEmpty()) {
            TrashBuilder.of(identifier, this.options).createHistory(content, params);
        }
        return Future.succeededFuture(records);
    }

    @Override
    public Future<JsonObject> backupAsync(final String identifier, final JsonObject record) {
        return this.backupAsync(identifier, record, null);
    }

    @Override
    public Future<JsonArray> backupAsync(final String identifier, final JsonArray records) {
        return this.backupAsync(identifier, records, null);
    }

    @Override
    public Future<JsonObject> restoreAsync(final String identifier, final JsonObject record, final MultiMap params) {
        /*
         * Wait for future
         */
        return null;
    }
}
