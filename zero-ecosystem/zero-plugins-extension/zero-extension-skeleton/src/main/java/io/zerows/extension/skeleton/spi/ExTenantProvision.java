package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 注册流程中的租户创建 SPI。
 */
public interface ExTenantProvision {

    /**
     * 根据注册输入创建或获取租户记录。
     *
     * @param input 注册输入
     * @return 租户数据
     */
    Future<JsonObject> provision(JsonObject input);
}
