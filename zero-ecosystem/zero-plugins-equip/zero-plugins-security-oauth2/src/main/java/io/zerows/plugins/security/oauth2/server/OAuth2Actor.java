package io.zerows.plugins.security.oauth2.server;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.epoch.constant.KName;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.OAuth2ServerActor;
import io.zerows.plugins.oauth2.metadata.OAuth2Credential;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;
import io.zerows.plugins.security.oauth2.server.service.AuthStub;
import io.zerows.plugins.security.oauth2.server.service.MetaStub;
import io.zerows.plugins.security.oauth2.server.service.OAuthTool;
import io.zerows.plugins.security.oauth2.server.service.TokenStub;
import io.zerows.support.Ut;
import jakarta.inject.Inject;

@Queue
public class OAuth2Actor {

    @Inject
    private MetaStub metaStub;

    @Inject
    private TokenStub tokenStub;

    @Inject
    private AuthStub authStub;

    @Address(Addr.AUTHORIZE)
    public Future<JsonObject> authorize(final JsonObject request) {
        return this.authStub.authorizeAsync(request);
    }

    @Address(Addr.TOKEN)
    public Future<JsonObject> token(final JsonObject body) {
        return this.tokenStub.tokenAsync(body);
    }

    @Address(Addr.JWKS)
    public Future<JsonObject> jwks(final JsonObject params) {
        return this.metaStub.jwksAsync();
    }

    @Address(Addr.REVOKE)
    public Future<JsonObject> revoke(final JsonObject body) {
        final OAuth2Credential credential = this.extract(body);
        if (credential == null || !credential.isValid()) {
            return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " REVOKE / 客户端认证失败，缺失凭证"));
        }
        final String token = Ut.valueString(body, KName.TOKEN);
        if (StrUtil.isBlank(token)) {
            // RFC 7009: 如果 token 参数为空，直接返回 200
            return Future.succeededFuture(new JsonObject());
        }
        return this.tokenStub.revokeAsync(body, credential);
    }

    @Address(Addr.INTROSPECT)
    public Future<JsonObject> introspect(final JsonObject body) {
        // 1. 客户端认证
        final OAuth2Credential credential = this.extract(body);
        if (credential == null || !credential.isValid()) {
            return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " INTROSPECT / 客户端认证失败，缺失凭证"));
        }
        final String token = Ut.valueString(body, KName.TOKEN);
        if (StrUtil.isBlank(token)) {
            // RFC 7009: 如果 token 参数为空，直接返回 200
            return Future.succeededFuture(new JsonObject().put(KName.ACTIVE, false));
        }
        return this.tokenStub.introspectAsync(token, credential);
    }

    @Address(Addr.USERINFO)
    public Future<JsonObject> userinfo(final String authorization) {
        final OAuth2Security security = OAuth2ServerActor.securityOf();
        // 1. 提取 Bearer Token
        if (StrUtil.isBlank(authorization) || !authorization.startsWith(security.getTokenType() + " ")) {
            return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + "缺少 " + security.getTokenType() + " Token"));
        }
        final String accessToken = authorization.substring(security.getTokenType().length() + 1);
        return this.metaStub.userinfoAsync(accessToken);
    }

    private OAuth2Credential extract(final JsonObject request) {
        final String auth = request.getString("Authorization");
        if (StrUtil.isNotEmpty(auth)) {
            return OAuthTool.fromHeader(auth);
        }
        return new OAuth2Credential(
            request.getString(OAuth2Constant.CLIENT_ID),
            request.getString(OAuth2Constant.CLIENT_SECRET)
        );
    }
}
