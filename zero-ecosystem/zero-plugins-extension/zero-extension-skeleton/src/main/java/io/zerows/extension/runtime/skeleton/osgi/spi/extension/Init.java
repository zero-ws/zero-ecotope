package io.zerows.extension.runtime.skeleton.osgi.spi.extension;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.support.Ut;

import java.util.function.Function;

/**
 * ## 「Init」Initializing Uniform Interface
 *
 * OOB Data initialization for The whole application.
 * 1) XApp: EmApp Data
 * 2) XSource: Data Source Data
 * 3) Extension: Configuration For Initializer Extension for other flow.
 */
public interface Init {

    Cc<String, Init> CC_INIT = Cc.open();

    /*
     * Initializer generate method.
     */
    static Init generate(final Class<?> clazz) {
        return CC_INIT.pick(() -> Ut.instance(clazz), clazz.getName()); //
        // FnZero.po?l(Pool.INIT_POOL, clazz.getName(), () -> Ut.instance(clazz));
    }

    /*
     * Executor Constructor
     */
    Function<JsonObject, Future<JsonObject>> apply();

    /*
     * Unique condition for current object
     */
    default JsonObject whereUnique(final JsonObject input) {
        /* Default situation, nothing to do */
        return new JsonObject();
    }

    /*
     * Executor result hooker
     */
    default JsonObject result(final JsonObject input, final JsonObject appJson) {
        /* Default situation, return to appJson */
        return appJson;
    }
}
