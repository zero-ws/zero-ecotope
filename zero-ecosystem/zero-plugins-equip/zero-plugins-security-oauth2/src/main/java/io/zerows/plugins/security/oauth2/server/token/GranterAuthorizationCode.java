package io.zerows.plugins.security.oauth2.server.token;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2AuthorizationDao;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2Authorization;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2GrantType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 授权码模式 (Authorization Code)
 * 流程：
 * 1. 验证 Code 是否存在且有效
 * 2. 验证 Redirect URI 是否匹配
 * 3. 销毁 Code (一次性使用)
 * 4. 颁发 Access Token
 */
@Slf4j
class GranterAuthorizationCode extends GranterBase {

    @Override
    public OAuth2GrantType grantType() {
        return OAuth2GrantType.AUTHORIZATION_CODE;
    }

    @Override
    protected Future<String> validateGrant(final Oauth2RegisteredClient client, final JsonObject request) {
        // 1. 获取请求参数
        final String code = request.getString(OAuth2Constant.CODE);
        final String redirectUri = request.getString(OAuth2Constant.REDIRECT_URI);

        if (StrUtil.isBlank(code)) {
            return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 必须提供 code 参数"));
        }
        if (StrUtil.isBlank(redirectUri)) {
            return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 必须提供 redirect_uri 参数"));
        }

        // 2. 查询授权码记录
        // 注意：这里使用 POJO 属性名 "authorizationCodeValue"
        return DB.on(Oauth2AuthorizationDao.class)
            .<Oauth2Authorization>fetchOneAsync("authorizationCodeValue", code)
            .compose(auth -> {
                // 3. 各种校验

                // A. 是否存在
                if (auth == null) {
                    return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 无效的授权码"));
                }

                // B. 客户端是否匹配 (防止 Code 被截获后由另一个 Client 冒用)
                if (!auth.getRegisteredClientId().equals(client.getClientId())) {
                    return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 授权码不属于当前客户端"));
                }

                // C. 是否过期
                if (auth.getAuthorizationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                    return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 授权码已过期"));
                }

                // D. Redirect URI 是否匹配 (RFC 要求必须与获取 Code 时完全一致)
                if (!this.checkRedirectUri(auth, redirectUri)) {
                    return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " redirect_uri 与获取授权码时不匹配"));
                }

                // 4. 【关键】消耗授权码 (Delete)
                // Code 是一次性的，使用后必须立即删除，防止重放攻击
                return DB.on(Oauth2AuthorizationDao.class)
                    .deleteByIdAsync(auth.getId())
                    .compose(nil -> {
                        log.info("{} 授权码校验通过并已销毁 Code={}", OAuth2Constant.K_PREFIX, code);

                        // TODO: 这里的 auth.getPrincipalName() 是 "zero-user"，
                        // 目前 GranterBase 生成 Token 时默认使用了 ClientId 作为 sub，
                        // 后续若需支持用户维度，需调整 GranterBase 或在此处返回更多上下文。

                        // 返回当时授权的 Scope
                        return Future.succeededFuture(auth.getAuthorizedScopes());
                    });
            });
    }

    /**
     * 校验重定向地址
     * 需要解析当初存入数据库的 attributes JSON
     */
    private boolean checkRedirectUri(final Oauth2Authorization auth, final String requestedUri) {
        if (StrUtil.isBlank(auth.getAttributes())) {
            return false;
        }
        try {
            final JsonObject attributes = new JsonObject(auth.getAttributes());
            final String originalUri = attributes.getString(OAuth2Constant.REDIRECT_URI);
            // 必须完全相等
            return StrUtil.equals(originalUri, requestedUri);
        } catch (final Exception e) {
            log.error("解析授权属性失败", e);
            return false;
        }
    }
}