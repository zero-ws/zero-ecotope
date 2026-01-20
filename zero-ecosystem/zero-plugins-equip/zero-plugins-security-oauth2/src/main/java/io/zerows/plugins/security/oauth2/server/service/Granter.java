package io.zerows.plugins.security.oauth2.server.service;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.oauth2.metadata.OAuth2GrantType;

/**
 * 授权策略接口
 * 每个实现类负责一种特定的 Grant Type (e.g., client_credentials, authorization_code)
 */
public interface Granter {
    Cc<String, Granter> CC_SKELETON = Cc.openThread();

    static Granter of(final OAuth2GrantType type) {
        if (OAuth2GrantType.CLIENT_CREDENTIALS == type) {
            return CC_SKELETON.pick(GranterClientCredentials::new, type.name());
        }
        return null;
    }

    /**
     * 该实现支持哪种授权模式
     */
    OAuth2GrantType grantType();

    /**
     * 执行授权逻辑
     *
     * @param request HTTP 请求参数
     * @return 包含 access_token 的响应
     */
    Future<JsonObject> grantAsync(JsonObject request);
}