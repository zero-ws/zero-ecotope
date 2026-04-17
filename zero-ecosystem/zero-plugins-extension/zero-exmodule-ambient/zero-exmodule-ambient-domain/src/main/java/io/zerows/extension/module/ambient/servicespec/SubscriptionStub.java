package io.zerows.extension.module.ambient.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Subscription Special Operations Service Interface
 *
 * @author lang : 2026-02-14
 */
public interface SubscriptionStub {

    /**
     * 购买应用，购买关系复用 X_SUBSCRIPTION 表承载
     *
     * @param tenantId 租户ID
     * @param sigma    统一标识
     * @param input    购买参数
     * @return 已购应用摘要
     */
    Future<JsonObject> purchaseApp(String tenantId, String sigma, JsonObject input);

    /**
     * 获取订阅仪表板统计数据
     *
     * @param sigma 统一标识
     * @return 仪表板数据（活跃订阅数、月收入、待处理账单等）
     */
    Future<JsonObject> fetchDashboard(String sigma);

    /**
     * 取消订阅
     * 将订阅状态设置为 CANCELLED
     *
     * @param subId 订阅ID
     * @return 更新后的订阅信息
     */
    Future<JsonObject> cancelSubscription(String subId);

    /**
     * 续费订阅
     * 延长订阅的结束时间
     *
     * @param subId 订阅ID
     * @return 更新后的订阅信息
     */
    Future<JsonObject> renewSubscription(String subId);

    /**
     * 获取当前用户的订阅列表（个人订阅）
     *
     * @param userId 用户ID（SUser.ID）
     * @return 订阅列表
     */
    Future<JsonArray> fetchMySubscriptions(String userId);

    /**
     * 查询当前租户的已购应用列表
     *
     * @param tenantId 租户ID
     * @param query    查询参数
     * @return 已购应用列表与总数
     */
    Future<JsonObject> searchPurchased(String tenantId, JsonObject query);
}
