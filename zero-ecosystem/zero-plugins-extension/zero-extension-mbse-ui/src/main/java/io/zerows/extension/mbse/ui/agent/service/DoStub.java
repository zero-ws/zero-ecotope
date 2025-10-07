package io.zerows.extension.mbse.ui.agent.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * --> type = null
 * -----> control     = null,         WEB
 * -----> control     = get,        ATOM
 * --> type = ATOM
 * -----> control     = get
 * --> type = WEB
 * -----> identifier  = get
 * --> type = FLOW
 * -----> control     = get,
 * -----> event       = 任务节点
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface DoStub {
    /*
     * fetchAtom 和 fetchWeb 的切换（遗留系统专用）
     * type = null, 原始流程，根据 control 判断
     */
    Future<JsonArray> fetchSmart(JsonObject params);

    /*
     * 参数中必须包含:
     * {
     *     "control": "控件ID"
     * }
     **/
    Future<JsonArray> fetchAtom(JsonObject params);

    /*
     * 参数中必须包含:
     * {
     *     "identifier": "模型ID"
     * }
     */
    Future<JsonArray> fetchWeb(JsonObject params);

    /*
     * 参数中必须包含:
     * {
     *     "control": "工作流名称",
     *     "event":   "task任务节点名称"
     * }
     */
    Future<JsonArray> fetchFlow(JsonObject params);
}
