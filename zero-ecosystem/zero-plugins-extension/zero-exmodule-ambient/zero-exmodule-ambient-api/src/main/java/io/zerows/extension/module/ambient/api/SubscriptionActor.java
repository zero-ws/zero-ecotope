package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
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
