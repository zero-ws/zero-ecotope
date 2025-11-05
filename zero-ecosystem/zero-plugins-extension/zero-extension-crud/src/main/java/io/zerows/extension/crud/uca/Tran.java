package io.zerows.extension.crud.uca;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.crud.common.Pooled;
import io.zerows.platform.exception._60050Exception501NotSupport;

/*
 * {
 *      "transform": {
 *      }
 * }
 * Processing for "transform"
 */
public interface Tran {

    static Tran fabric(final boolean isFrom) {
        return Pooled.CCT_TRAN.pick(() -> new TranFabric(isFrom), TranFabric.class.getName() + isFrom);
    }

    static Tran tree(final boolean isFrom) {
        return Pooled.CCT_TRAN.pick(() -> new TranTree(isFrom), TranTree.class.getName() + isFrom);
    }

    static Tran map(final boolean isFrom) {
        return Pooled.CCT_TRAN.pick(() -> new TranMap(isFrom), TranMap.class.getName() + isFrom);
    }

    static Tran initial() {
        return Pooled.CCT_TRAN.pick(TranInitial::new, TranInitial.class.getName());
    }

    // JsonObject -> JsonObject
    default Future<JsonObject> inJAsync(final JsonObject data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    // JsonArray -> JsonArray
    default Future<JsonArray> inAAsync(final JsonArray data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    // JsonArray -> JsonObject
    default Future<JsonObject> inAJAsync(final JsonArray data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }

    // JsonObject -> JsonArray
    default Future<JsonArray> inJAAsync(final JsonObject data, final IxMod in) {
        return FnVertx.failOut(_60050Exception501NotSupport.class, this.getClass());
    }
}
