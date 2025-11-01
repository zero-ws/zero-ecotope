package io.zerows.boot.graphic;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.extension.util.Ox;

public class PixelNodeAdd extends PixelBase {

    public PixelNodeAdd(final String identifier) {
        super(identifier);
    }

    @Override
    public Future<JsonObject> drawAsync(final JsonObject item) {
        return this.runSafe(Ox.toNode(item), this.client::nodeCreate);
    }

    @Override
    public Future<JsonArray> drawAsync(final JsonArray item) {
        return this.runSafe(Ox.toNode(item), this.client::nodeCreate);
    }
}
