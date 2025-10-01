package io.zerows.epoch.common.uca.aop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.typed.ChangeFlag;

import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface After {

    Set<ChangeFlag> types();

    /*
     * After Operation
     */
    default Future<JsonObject> afterAsync(final JsonObject data, final JsonObject config) {
        return Future.succeededFuture(data);
    }

    default Future<JsonArray> afterAsync(final JsonArray data, final JsonObject config) {
        return Future.succeededFuture(data);
    }
}
