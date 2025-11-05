package io.zerows.extension.module.ui.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ui.common.em.ControlType;

public interface ControlStub {
    /*
     * Fetch controls by pageId
     */
    Future<JsonArray> fetchControls(String pageId);

    /*
     * Fetch control by id
     */
    Future<JsonObject> fetchById(String control);

    /*
     * Fetch control based join UI_VISITOR
     */
    Future<JsonObject> fetchControl(ControlType controlType, JsonObject params);
}
