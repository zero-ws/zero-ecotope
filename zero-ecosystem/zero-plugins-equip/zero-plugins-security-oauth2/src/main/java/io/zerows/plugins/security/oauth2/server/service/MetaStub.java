package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface MetaStub {
    /**
     * 获取 JWK Set (公钥集合)
     */
    Future<JsonObject> jwksAsync();

    /**
     * 获取用户信息 (OIDC)
     *
     * @param accessToken 已提取的纯净 Access Token 字符串
     */
    Future<JsonObject> userinfoAsync(String accessToken);
}
