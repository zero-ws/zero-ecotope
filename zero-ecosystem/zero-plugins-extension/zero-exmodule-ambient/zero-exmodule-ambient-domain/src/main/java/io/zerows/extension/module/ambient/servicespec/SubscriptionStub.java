package io.zerows.extension.module.ambient.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Subscription Special Operations Service Interface
 *
 * @author lang : 2026-02-14
 */
public interface SubscriptionStub {

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
}
