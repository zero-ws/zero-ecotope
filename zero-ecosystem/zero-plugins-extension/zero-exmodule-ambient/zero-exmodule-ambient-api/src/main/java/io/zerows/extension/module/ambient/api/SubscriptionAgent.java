package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import io.zerows.epoch.constant.KWeb;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

/**
 * Subscription Special Operations REST API
 *
 * @author lang : 2026-02-14
 */
@EndPoint
@Path("/api/subscription")
public interface SubscriptionAgent {

    /**
     * 获取订阅仪表板统计数据
     * GET /api/subscription/dashboard
     *
     * @param sigma 统一标识
     * @return 仪表板数据
     */
    @Path("/dashboard")
    @GET
    @Address(Addr.Subscription.DASHBOARD)
    @OpenApi
    JsonObject getDashboard(@HeaderParam(KWeb.HEADER.X_SIGMA) String sigma);

    /**
     * 取消订阅
     * POST /api/subscription/subscriptions/{id}/cancel
     *
     * @param subId 订阅ID
     * @return 更新后的订阅信息
     */
    @Path("/subscriptions/{id}/cancel")
    @POST
    @Address(Addr.Subscription.CANCEL)
    @OpenApi
    JsonObject cancelSubscription(@PathParam("id") String subId);

    /**
     * 续费订阅
     * POST /api/subscription/subscriptions/{id}/renew
     *
     * @param subId 订阅ID
     * @return 更新后的订阅信息
     */
    @Path("/subscriptions/{id}/renew")
    @POST
    @Address(Addr.Subscription.RENEW)
    @OpenApi
    JsonObject renewSubscription(@PathParam("id") String subId);
}
