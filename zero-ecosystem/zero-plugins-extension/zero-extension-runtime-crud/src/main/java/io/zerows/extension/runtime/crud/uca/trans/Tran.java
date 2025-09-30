package io.zerows.extension.runtime.crud.uca.trans;

import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.web._60050Exception501NotSupport;
import io.zerows.extension.runtime.crud.eon.Pooled;
import io.zerows.extension.runtime.crud.uca.desk.IxMod;

/*
 * {
 *      "transform": {
 *      }
 * }
 * Processing for "transform"
 */
public interface Tran {

    static Tran fabric(final boolean isFrom) {
        return Pooled.CCT_TRAN.pick(() -> new FabricTran(isFrom), FabricTran.class.getName() + isFrom);
    }

    static Tran tree(final boolean isFrom) {
        return Pooled.CCT_TRAN.pick(() -> new TreeTran(isFrom), TreeTran.class.getName() + isFrom);
    }

    static Tran map(final boolean isFrom) {
        return Pooled.CCT_TRAN.pick(() -> new MapTran(isFrom), MapTran.class.getName() + isFrom);
    }

    static Tran initial() {
        return Pooled.CCT_TRAN.pick(InitialTran::new, InitialTran.class.getName());
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
