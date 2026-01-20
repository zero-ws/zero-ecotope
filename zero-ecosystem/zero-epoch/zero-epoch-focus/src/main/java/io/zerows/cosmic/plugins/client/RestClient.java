package io.zerows.cosmic.plugins.client;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.plugins.AddOn;

/**
 * 统一 REST 客户端接口
 * 使用 default 方法提供便捷重载，实现类只需关注核心逻辑。
 */
@AddOn.Name("DEFAULT_REST_CLIENT")
public interface RestClient {
    /**
     * 工厂方法
     */
    static RestClient createClient(final Vertx vertx, final RestClientConfig config) {
        return new RestClientImpl(vertx, config);
    }

    // =================================================================================
    // GET 请求 (JsonObject 代表 Query Params)
    // =================================================================================

    /**
     * 核心 GET 方法 (需实现)
     */
    Future<JsonObject> doGet(String uri, JsonObject queryParams, MultiMap headers);

    default Future<JsonObject> doGet(final String uri) {
        return this.doGet(uri, null, null);
    }

    default Future<JsonObject> doGet(final String uri, final JsonObject queryParams) {
        return this.doGet(uri, queryParams, null);
    }

    // =================================================================================
    // POST 请求 (JsonObject 代表 Body)
    // =================================================================================

    /**
     * 核心 POST 方法 (需实现)
     */
    Future<JsonObject> doPost(String uri, JsonObject body, MultiMap headers);

    default Future<JsonObject> doPost(final String uri, final JsonObject body) {
        return this.doPost(uri, body, null);
    }

    // -----------------------------------------------------------
    // 表单提交 (application/x-www-form-urlencoded)
    // -----------------------------------------------------------

    /**
     * 核心 Form 方法 (需实现)
     */
    Future<JsonObject> doPostForm(String uri, JsonObject formParams, MultiMap headers);

    default Future<JsonObject> doPostForm(final String uri, final JsonObject formParams) {
        return this.doPostForm(uri, formParams, null);
    }

    // =================================================================================
    // PUT 请求
    // =================================================================================

    /**
     * 核心 PUT 方法 (需实现)
     */
    Future<JsonObject> doPut(String uri, JsonObject body, MultiMap headers);

    default Future<JsonObject> doPut(final String uri, final JsonObject body) {
        return this.doPut(uri, body, null);
    }

    // =================================================================================
    // PATCH 请求
    // =================================================================================

    /**
     * 核心 PATCH 方法 (需实现)
     */
    Future<JsonObject> doPatch(String uri, JsonObject body, MultiMap headers);

    default Future<JsonObject> doPatch(final String uri, final JsonObject body) {
        return this.doPatch(uri, body, null);
    }

    // =================================================================================
    // DELETE 请求
    // =================================================================================

    /**
     * 核心 DELETE 方法 (需实现)
     */
    Future<JsonObject> doDelete(String uri, JsonObject queryParams, MultiMap headers);

    default Future<JsonObject> doDelete(final String uri) {
        return this.doDelete(uri, null, null);
    }

    default Future<JsonObject> doDelete(final String uri, final JsonObject queryParams) {
        return this.doDelete(uri, queryParams, null);
    }

    // =================================================================================
    // 其他方法 (OPTIONS, HEAD)
    // =================================================================================

    Future<JsonObject> doOptions(String uri, MultiMap headers);

    default Future<JsonObject> doOptions(final String uri) {
        return this.doOptions(uri, null);
    }

    Future<JsonObject> doHead(String uri, MultiMap headers);

    default Future<JsonObject> doHead(final String uri) {
        return this.doHead(uri, null);
    }
}