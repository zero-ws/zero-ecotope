package io.zerows.extension.mbse.ui.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.mbse.ui.eon.em.ControlType;

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
     * Fetch control based on UI_VISITOR
     */
    Future<JsonObject> fetchControl(ControlType controlType, JsonObject params);
}
