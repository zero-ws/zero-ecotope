package io.zerows.extension.module.ambient.servicespec;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Tenant Management Service Interface
 *
 * @author lang : 2026-02-14
 */
public interface TenantStub {

    /**
     * 激活租户
     * 将租户状态设置为 ACTIVE，active 字段设置为 true
     *
     * @param tenantKey 租户ID或唯一标识
     * @param reason 激活原因（可选）
     * @return 更新后的租户信息
     */
    Future<JsonObject> activate(String tenantKey, String reason);

    /**
     * 冻结租户
     * 将租户状态设置为 FROZEN，active 字段设置为 false
     *
     * @param tenantKey 租户ID或唯一标识
     * @param reason 冻结原因（必填，10-500字符）
     * @return 更新后的租户信息
     */
    Future<JsonObject> deactivate(String tenantKey, String reason);

    /**
     * 获取租户状态变更历史
     * 分页查询租户的状态变更记录
     *
     * @param tenantKey 租户ID或唯一标识
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 状态变更历史列表
     */
    Future<JsonArray> fetchStatusHistory(String tenantKey, Integer page, Integer size);

    /**
     * 获取租户配额信息
     * 返回租户的资源配额限制、当前使用量和使用率
     *
     * @param tenantKey 租户ID或唯一标识
     * @return 配额信息（包含限制、使用量、使用率）
     */
    Future<JsonObject> fetchQuota(String tenantKey);

    /**
     * 更新租户配额
     * 修改租户的资源配额限制，新配额不能低于当前使用量
     *
     * @param tenantKey 租户ID或唯一标识
     * @param quotaData 新的配额数据
     * @param reason 更新原因（必填）
     * @return 更新后的配额信息
     */
    Future<JsonObject> updateQuota(String tenantKey, JsonObject quotaData, String reason);

    /**
     * 获取租户统计信息
     * 返回租户的用户数、应用数、存储使用量、带宽使用量、API调用次数等统计数据
     *
     * @param tenantKey 租户ID或唯一标识
     * @return 租户统计信息
     */
    Future<JsonObject> fetchStatistics(String tenantKey);
}
