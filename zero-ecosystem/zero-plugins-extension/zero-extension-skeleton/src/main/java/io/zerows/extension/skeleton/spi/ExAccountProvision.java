package io.zerows.extension.skeleton.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * 为动态注册流程预创建账号与默认角色关系。
 */
public interface ExAccountProvision {

    /**
     * 根据注册输入创建或补齐账号信息。
     *
     * @param input 注册输入
     * @return 已创建或已存在的账号数据
     */
    Future<JsonObject> provision(JsonObject input);
}
