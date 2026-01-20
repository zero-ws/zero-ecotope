package io.zerows.plugins.security.oauth2.server.token;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2GrantType;
import lombok.extern.slf4j.Slf4j;

/**
 * Client Credentials 模式实现
 * 逻辑：最简单，只要客户端认证通过（基类已做），直接协商 Scope 即可。
 */
@Slf4j
class GranterClientCredentials extends GranterBase {

    @Override
    public OAuth2GrantType grantType() {
        return OAuth2GrantType.CLIENT_CREDENTIALS;
    }

    @Override
    protected Future<String> validateGrant(final Oauth2RegisteredClient client, final JsonObject request) {
        // 1. 获取请求的 Scope
        final String requestedScope = request.getString(OAuth2Constant.SCOPE);

        // 2. 协商 Scope (取交集)
        final String finalScope = this.negotiateScope(client.getScopes(), requestedScope);

        // 3. 返回最终 Scope 供基类生成 Token
        return Future.succeededFuture(finalScope);
    }
}