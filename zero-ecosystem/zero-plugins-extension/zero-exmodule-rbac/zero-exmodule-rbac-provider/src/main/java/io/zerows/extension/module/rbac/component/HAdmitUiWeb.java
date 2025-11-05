package io.zerows.extension.module.rbac.component;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.program.Ux;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class HAdmitUiWeb implements HAdmit {
    @Override
    public Future<JsonArray> ingest(final JsonObject qr, final JsonObject config) {
        return Ux.futureA();
    }
}
