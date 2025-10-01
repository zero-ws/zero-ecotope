package io.zerows.extension.runtime.crud.uca.next;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class OkAActive implements Co<JsonObject, JsonArray, Object, JsonArray> {
    @Override
    public Future<JsonArray> ok(final JsonArray active, final Object standBy) {
        return Ux.future(active);
    }
}
