package io.zerows.module.feature.toolkit.expression.wffs;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.web._60050Exception501NotSupport;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Hooker {

    Hooker bind(JsonObject config);

    boolean async();

    Future<JsonObject> execAsync(JsonObject data);

    default Future<JsonArray> execAsync(final JsonArray data) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
