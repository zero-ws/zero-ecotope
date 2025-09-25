package io.zerows.module.feature.toolkit.expression.wffs;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.util.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Hooker {

    Hooker bind(JsonObject config);

    boolean async();

    Future<JsonObject> execAsync(JsonObject data);

    default Future<JsonArray> execAsync(final JsonArray data) {
        return Ut.Bnd.failOut(_501NotSupportException.class, this.getClass());
    }
}
