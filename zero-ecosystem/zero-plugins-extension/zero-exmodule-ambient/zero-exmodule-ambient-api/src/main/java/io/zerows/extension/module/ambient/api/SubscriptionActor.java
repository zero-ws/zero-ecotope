package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.ambient.servicespec.SubscriptionStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

/**
 * Subscription Special Operations Event Bus Actor
 *
 * @author lang : 2026-02-14
 */
@Queue
public class SubscriptionActor {

    @Inject
    private SubscriptionStub subscriptionStub;

    /**
     * 在线购买应用
     *
     * @param tenantId 租户ID
     * @param sigma    统一标识
     * @param body     购买参数
     * @return 已购应用摘要
     */
    @Address(Addr.Subscription.PURCHASE)
    public Future<JsonObject> purchaseApp(final String tenantId, final String sigma, final JsonObject body) {
        if (Ut.isNil(tenantId)) {
            return Ux.future(new JsonObject().put("error", "Tenant ID is required"));
        }
        if (body == null || Ut.isNil(body.getString("appId"))) {
            return Ux.future(new JsonObject().put("error", "Application ID is required"));
        }
        return this.subscriptionStub.purchaseApp(tenantId, sigma, body);
    }

    /**
     * 获取订阅仪表板统计数据
     *
     * @param sigma 统一标识
     * @return 仪表板数据
     */
    @Address(Addr.Subscription.DASHBOARD)
    public Future<JsonObject> getDashboard(final String sigma) {
        if (Ut.isNil(sigma)) {
            return Ux.future(new JsonObject());
        }
        return this.subscriptionStub.fetchDashboard(sigma);
    }

    /**
     * 获取当前用户的订阅列表（个人订阅）
     *
     * @param envelop 请求上下文，包含 userId
     * @return 订阅列表
     */
    @Address(Addr.Subscription.MY_SUBSCRIPTIONS)
    public Future<JsonObject> getMySubscriptions(final Envelop envelop) {
        final String userId = envelop.userId();
        if (Ut.isNil(userId)) {
            return Ux.future(new JsonObject().put("list", new JsonArray()));
        }
        return this.subscriptionStub.fetchMySubscriptions(userId)
            .compose(array -> Ux.future(new JsonObject().put("list", array)));
    }

    /**
     * 查询当前租户已购应用列表
     *
     * @param tenantId 租户ID
     * @param body     查询参数
     * @return 已购应用列表
     */
    @Address(Addr.Subscription.PURCHASED_SEARCH)
    public Future<JsonObject> searchPurchased(final String tenantId, final JsonObject body) {
        if (Ut.isNil(tenantId)) {
            return Ux.future(new JsonObject().put("list", new JsonArray()).put("count", 0));
        }
        return this.subscriptionStub.searchPurchased(tenantId, body);
    }

    /**
     * 取消订阅
     *
     * @param subId 订阅ID
     * @return 更新后的订阅信息
     */
    @Address(Addr.Subscription.CANCEL)
    public Future<JsonObject> cancelSubscription(final String subId) {
        if (Ut.isNil(subId)) {
            return Ux.future(new JsonObject().put("error", "Subscription ID is required"));
        }
        return this.subscriptionStub.cancelSubscription(subId);
    }

    /**
     * 续费订阅
     *
     * @param subId 订阅ID
     * @return 更新后的订阅信息
     */
    @Address(Addr.Subscription.RENEW)
    public Future<JsonObject> renewSubscription(final String subId) {
        if (Ut.isNil(subId)) {
            return Ux.future(new JsonObject().put("error", "Subscription ID is required"));
        }
        return this.subscriptionStub.renewSubscription(subId);
    }
}
