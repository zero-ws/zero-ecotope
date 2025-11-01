package io.zerows.epoch.bootplus.extension.uca.graphic;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.extension.util.Ox;

public class PixelNodeDelete extends PixelBase {

    public PixelNodeDelete(final String identifier) {
        super(identifier);
    }

    @Override
    public Future<JsonObject> drawAsync(final JsonObject item) {
        return this.runSafe(Ox.toNode(item), this.client::nodeRemove);
    }

    @Override
    public Future<JsonArray> drawAsync(final JsonArray item) {
        return this.runSafe(Ox.toNode(item), this.client::nodeRemove);
    }
}
