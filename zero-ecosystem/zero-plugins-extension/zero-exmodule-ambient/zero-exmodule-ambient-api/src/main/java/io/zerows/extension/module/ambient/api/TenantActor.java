package io.zerows.extension.module.ambient.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.ambient.servicespec.TenantStub;
import io.zerows.program.Ux;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

/**
 * Tenant Management Event Bus Actor
 *
 * @author lang : 2026-02-14
 */
@Queue
public class TenantActor {

    @Inject
    private TenantStub tenantStub;

    /**
     * 激活租户
     *
     * @param tenantKey 租户ID或唯一标识
     * @param body 请求体，包含 reason 字段（可选）
     * @return 更新后的租户信息
     */
    @Address(Addr.Tenant.ACTIVATE)
    public Future<JsonObject> activate(final String tenantKey, final JsonObject body) {
        if (Ut.isNil(tenantKey)) {
            return Ux.future(new JsonObject().put("error", "Tenant key is required"));
        }
        final String reason = body == null ? null : body.getString("reason");
        return this.tenantStub.activate(tenantKey, reason);
    }

    /**
     * 冻结租户
     *
     * @param tenantKey 租户ID或唯一标识
     * @param body 请求体，包含 reason 字段（必填，10-500字符）
     * @return 更新后的租户信息
     */
    @Address(Addr.Tenant.DEACTIVATE)
    public Future<JsonObject> deactivate(final String tenantKey, final JsonObject body) {
        if (Ut.isNil(tenantKey)) {
            return Ux.future(new JsonObject().put("error", "Tenant key is required"));
        }
        if (body == null || Ut.isNil(body.getString("reason"))) {
            return Ux.future(new JsonObject().put("error", "Reason is required"));
        }
        final String reason = body.getString("reason");
        if (reason.length() < 10 || reason.length() > 500) {
            return Ux.future(new JsonObject().put("error", "Reason must be between 10 and 500 characters"));
        }
        return this.tenantStub.deactivate(tenantKey, reason);
    }

    /**
     * 获取租户状态变更历史
     *
     * @param tenantKey 租户ID或唯一标识
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 状态变更历史列表
     */
    @Address(Addr.Tenant.STATUS_HISTORY)
    public Future<JsonArray> fetchStatusHistory(final String tenantKey, final Integer page, final Integer size) {
        if (Ut.isNil(tenantKey)) {
            return Ux.futureA();
        }
        final Integer pageNum = page == null || page < 1 ? 1 : page;
        final Integer pageSize = size == null || size < 1 ? 20 : size;
        return this.tenantStub.fetchStatusHistory(tenantKey, pageNum, pageSize);
    }

    /**
     * 获取租户配额信息
     *
     * @param tenantKey 租户ID或唯一标识
     * @return 配额信息（包含限制、使用量、使用率）
     */
    @Address(Addr.Tenant.QUOTA_GET)
    public Future<JsonObject> fetchQuota(final String tenantKey) {
        if (Ut.isNil(tenantKey)) {
            return Ux.future(new JsonObject());
        }
        return this.tenantStub.fetchQuota(tenantKey);
    }

    /**
     * 更新租户配额
     *
     * @param tenantKey 租户ID或唯一标识
     * @param body 请求体，包含 quota 和 reason 字段
     * @return 更新后的配额信息
     */
    @Address(Addr.Tenant.QUOTA_UPDATE)
    public Future<JsonObject> updateQuota(final String tenantKey, final JsonObject body) {
        if (Ut.isNil(tenantKey)) {
            return Ux.future(new JsonObject().put("error", "Tenant key is required"));
        }
        if (body == null || body.getJsonObject("quota") == null) {
            return Ux.future(new JsonObject().put("error", "Quota data is required"));
        }
        if (Ut.isNil(body.getString("reason"))) {
            return Ux.future(new JsonObject().put("error", "Reason is required"));
        }
        final JsonObject quotaData = body.getJsonObject("quota");
        final String reason = body.getString("reason");
        return this.tenantStub.updateQuota(tenantKey, quotaData, reason);
    }

    /**
     * 获取租户统计信息
     *
     * @param tenantKey 租户ID或唯一标识
     * @return 租户统计信息
     */
    @Address(Addr.Tenant.STATISTICS)
    public Future<JsonObject> fetchStatistics(final String tenantKey) {
        if (Ut.isNil(tenantKey)) {
            return Ux.future(new JsonObject());
        }
        return this.tenantStub.fetchStatistics(tenantKey);
    }
}
