package io.zerows.extension.crud.uca.next;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class OkActiveJJ_J implements Co<JsonObject, JsonObject, Object, JsonObject> {
    @Override
    public Future<JsonObject> ok(final JsonObject active, final Object standBy) {
        return Ux.future(active);
    }
}
