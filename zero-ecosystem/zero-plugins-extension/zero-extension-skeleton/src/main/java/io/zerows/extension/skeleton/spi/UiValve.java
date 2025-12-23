package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Journal Interface to of the history record
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface UiValve {
    /*
     * This method will read the list by `criteria` to web
     * the specification data structure `Regulation`, the Regulation contains following parts:
     * 1 - The parameter definition
     * 2 - The rule configuration
     * 3 - The expression checking
     *
     * Here the record contains:
     * 1) __data: original data
     * 2) __flag: flag findRunning here
     */
    Future<JsonObject> execAsync(JsonObject data, JsonObject config);
}
