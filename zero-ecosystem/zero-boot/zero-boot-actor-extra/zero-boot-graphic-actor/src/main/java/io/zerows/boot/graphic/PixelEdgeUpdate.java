package io.zerows.boot.graphic;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.boot.extension.util.Ox;

public class PixelEdgeUpdate extends PixelBase {

    public PixelEdgeUpdate(final String identifier) {
        super(identifier);
    }

    @Override
    public Future<JsonObject> drawAsync(final JsonObject item) {
        return this.runSafe(Ox.toEdge(item), this.client::edgeUpdate);
    }

    @Override
    public Future<JsonArray> drawAsync(final JsonArray item) {
        return this.runSafe(Ox.toEdge(item), this.client::edgeUpdate);
    }
}
