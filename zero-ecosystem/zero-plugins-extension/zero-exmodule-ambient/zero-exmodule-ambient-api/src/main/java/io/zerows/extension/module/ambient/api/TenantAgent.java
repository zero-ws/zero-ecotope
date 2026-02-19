package io.zerows.extension.module.ambient.api;

import io.r2mo.openapi.annotations.OpenApi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.EndPoint;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.extension.BodyParam;

/**
 * Tenant Management REST API
 *
 * @author lang : 2026-02-14
 */
@EndPoint
@Path("/api")
public interface TenantAgent {

    /**
     * 激活租户
     * POST /api/tenant/{key}/activate
     *
     * @param tenantKey 租户ID或唯一标识
     * @param body 请求体，包含 reason 字段（可选）
     * @return 更新后的租户信息
     */
    @Path("/tenant/{key}/activate")
    @POST
    @Address(Addr.Tenant.ACTIVATE)
    @OpenApi
    JsonObject activate(@PathParam("key") String tenantKey,
                        @BodyParam JsonObject body);

    /**
     * 冻结租户
     * POST /api/tenant/{key}/deactivate
     *
     * @param tenantKey 租户ID或唯一标识
     * @param body 请求体，包含 reason 字段（必填，10-500字符）
     * @return 更新后的租户信息
     */
    @Path("/tenant/{key}/deactivate")
    @POST
    @Address(Addr.Tenant.DEACTIVATE)
    @OpenApi
    JsonObject deactivate(@PathParam("key") String tenantKey,
                          @BodyParam JsonObject body);

    /**
     * 获取租户状态变更历史
     * GET /api/tenant/{key}/status/history
     *
     * @param tenantKey 租户ID或唯一标识
     * @param page 页码（从1开始，默认1）
     * @param size 每页大小（默认20）
     * @return 状态变更历史列表
     */
    @Path("/tenant/{key}/status/history")
    @GET
    @Address(Addr.Tenant.STATUS_HISTORY)
    @OpenApi
    JsonArray fetchStatusHistory(@PathParam("key") String tenantKey,
                                  @QueryParam("page") Integer page,
                                  @QueryParam("size") Integer size);

    /**
     * 获取租户配额信息
     * GET /api/tenant/{key}/quota
     *
     * @param tenantKey 租户ID或唯一标识
     * @return 配额信息（包含限制、使用量、使用率）
     */
    @Path("/tenant/{key}/quota")
    @GET
    @Address(Addr.Tenant.QUOTA_GET)
    @OpenApi
    JsonObject fetchQuota(@PathParam("key") String tenantKey);

    /**
     * 更新租户配额
     * PUT /api/tenant/{key}/quota
     *
     * @param tenantKey 租户ID或唯一标识
     * @param body 请求体，包含 quota 和 reason 字段
     * @return 更新后的配额信息
     */
    @Path("/tenant/{key}/quota")
    @PUT
    @Address(Addr.Tenant.QUOTA_UPDATE)
    @OpenApi
    JsonObject updateQuota(@PathParam("key") String tenantKey,
                           @BodyParam JsonObject body);

    /**
     * 获取租户统计信息
     * GET /api/tenant/{key}/statistics
     *
     * @param tenantKey 租户ID或唯一标识
     * @return 租户统计信息
     */
    @Path("/tenant/{key}/statistics")
    @GET
    @Address(Addr.Tenant.STATISTICS)
    @OpenApi
    JsonObject fetchStatistics(@PathParam("key") String tenantKey);
}
