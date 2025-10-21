package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface ExOwner {
    /**
     * 根据公司的 ID 提取公司相关信息，后期可能会变更
     *
     * @param id 公司 ID
     *
     * @return 公司信息
     */
    Future<JsonObject> fetchCompany(String id);

    /**
     * 根据 TenantId 或 Sigma 读取租户信息
     *
     * @param idOr TenantId 或 Sigma
     *
     * @return 租户信息
     */
    Future<JsonObject> fetchTenant(String idOr);
}
