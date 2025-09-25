package io.zerows.extension.commerce.documentation.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Set;

/**
 * 条款专用接口，处理条款相关信息
 *
 * @author lang : 2023-09-24
 */
public interface ClauseStub {

    Future<JsonArray> createAsync(JsonArray dataA, JsonObject record);

    Future<JsonArray> updateAsync(JsonArray dataA, JsonObject record);

    Future<JsonArray> fetchByDoc(String docKey);

    Future<Boolean> removeByKeys(String docKey, Set<String> keys);
}
