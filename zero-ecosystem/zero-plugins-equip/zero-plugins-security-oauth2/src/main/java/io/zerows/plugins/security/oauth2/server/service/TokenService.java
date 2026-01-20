package io.zerows.plugins.security.oauth2.server.service;

import cn.hutool.core.util.StrUtil;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2AuthorizationDao;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2RegisteredClientDao;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2Authorization;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2Credential;
import io.zerows.plugins.oauth2.metadata.OAuth2GrantType;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 令牌服务端点服务 (Dispatcher & Management)
 * <p>
 * 职责：
 * 1. tokenAsync: 路由分发 (Dispatcher)
 * 2. revokeAsync: 令牌撤销
 * 3. introspectAsync: 令牌内省
 */
@Slf4j
public class TokenService implements TokenStub {

    @Override
    public Future<JsonObject> tokenAsync(final JsonObject request) {
        // 1. 获取并解析授权模式
        final String grantTypeStr = request.getString(OAuth2Constant.GRANT_TYPE);
        log.info("{} 收到令牌请求，GrantType = {}", OAuth2Constant.K_PREFIX, grantTypeStr);

        // 2. 将字符串转换为枚举
        final OAuth2GrantType grantType = OAuth2GrantType.from(grantTypeStr);

        // 3. 尝试获取对应的授权器 (Granter)
        final Granter granter = (grantType != null) ? Granter.of(grantType) : null;

        // 4. 路由分发
        if (granter != null) {
            // 委托给具体的策略实现类 (如 GranterClientCredentials) 执行
            return granter.grantAsync(request);
        }

        // 5. 不支持的模式处理
        return Future.failedFuture(new _501NotSupportException(
            OAuth2Constant.K_PREFIX + " 暂不支持该授权模式: " + (grantTypeStr == null ? "null" : grantTypeStr)
        ));
    }

    // -------------------------------------------------------------------------
    // 1. 令牌撤销 (Revocation) - RFC 7009
    // -------------------------------------------------------------------------
    @Override
    public Future<JsonObject> revokeAsync(final JsonObject request, final OAuth2Credential credential) {

        return this.fetchClient(credential.getClientId()).compose(client -> {
            this.validateClientSecret(client, credential);

            // 2. 获取待撤销 Token
            final String tokenValue = request.getString(OAuth2Constant.TOKEN);
            final String typeHint = request.getString(OAuth2Constant.TOKEN_TYPE_HINT);

            // 3. 构建删除条件 (使用 POJO 属性名)
            final JsonObject criteria = new JsonObject();
            if (OAuth2Constant.REFRESH_TOKEN.equals(typeHint)) {
                criteria.put("refreshTokenValue", tokenValue);
            } else {
                // 默认尝试作为 Access Token 删除，或者如果 hint 不匹配也尝试删 AccessToken
                criteria.put("accessTokenValue", tokenValue);
            }

            // 4. 执行删除
            return DB.on(Oauth2AuthorizationDao.class)
                .deleteByAsync(criteria)
                .map(deleted -> {
                    log.info("{} (Revoke) 撤销 Token: {}, 结果: {}", OAuth2Constant.K_PREFIX, StrUtil.hide(tokenValue, 4, 24), deleted);
                    // 无论成功与否，RFC 7009 均要求返回 200 OK
                    return new JsonObject();
                });
        });
    }

    // -------------------------------------------------------------------------
    // 2. 令牌内省 (Introspection) - RFC 7662
    // -------------------------------------------------------------------------
    @Override
    public Future<JsonObject> introspectAsync(final String token, final OAuth2Credential credential) {
        return this.fetchClient(credential.getClientId()).compose(client -> {
            this.validateClientSecret(client, credential);
            // 3. 查库 (明确泛型 <Oauth2Authorization>)
            // 使用 POJO 属性名 "accessTokenValue"
            return DB.on(Oauth2AuthorizationDao.class)
                .<Oauth2Authorization>fetchOneAsync("accessTokenValue", token)
                .compose(auth -> {
                    final JsonObject response = new JsonObject();
                    boolean isActive = false;

                    // 4. 检查有效性
                    if (auth != null) {
                        final LocalDateTime expiresAt = auth.getAccessTokenExpiresAt();
                        // 检查是否过期
                        if (expiresAt != null && expiresAt.isAfter(LocalDateTime.now())) {
                            isActive = true;
                            response.put(OAuth2Constant.ACTIVE, true)
                                .put(OAuth2Constant.SUB, auth.getPrincipalName())
                                .put(OAuth2Constant.CLIENT_ID, auth.getRegisteredClientId())
                                .put(OAuth2Constant.SCOPE, auth.getAuthorizedScopes())
                                .put(OAuth2Constant.TOKEN_TYPE, auth.getAccessTokenType())
                                // 转为 Epoch Seconds
                                .put(OAuth2Constant.EXP, expiresAt.atZone(ZoneId.systemDefault()).toEpochSecond())
                                .put(OAuth2Constant.IAT, auth.getAccessTokenIssuedAt().atZone(ZoneId.systemDefault()).toEpochSecond());

                            // 补充 JTI (如果 attributes 中有)
                            if (StrUtil.isNotEmpty(auth.getAttributes())) {
                                final JsonObject attrs = new JsonObject(auth.getAttributes());
                                if (attrs.containsKey(OAuth2Constant.JTI)) {
                                    response.put(OAuth2Constant.JTI, attrs.getString(OAuth2Constant.JTI));
                                }
                            }
                        }
                    }

                    if (!isActive) {
                        response.put(OAuth2Constant.ACTIVE, false);
                    }

                    log.info("{} Introspect / 检查 Token: {} -> active: {}", OAuth2Constant.K_PREFIX, StrUtil.hide(token, 4, 24), isActive);
                    return Future.succeededFuture(response);
                });
        });
    }


    // -------------------------------------------------------------------------
    // 内部辅助方法
    // -------------------------------------------------------------------------

    /**
     * 查询客户端，使用泛型确保返回类型正确
     */
    private Future<Oauth2RegisteredClient> fetchClient(final String clientId) {
        return DB.on(Oauth2RegisteredClientDao.class)
            .<Oauth2RegisteredClient>fetchOneAsync(OAuth2Constant.Field.CLIENT_ID, clientId)
            .compose(client -> {
                if (client == null) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " 客户端不存在"));
                }
                return Future.succeededFuture(client);
            });
    }

    private void validateClientSecret(final Oauth2RegisteredClient client, final OAuth2Credential credential) {
        // 注意：生产环境建议使用 PasswordEncoder (如 BCrypt) 验证
        if (StrUtil.isNotEmpty(client.getClientSecret()) &&
            !client.getClientSecret().equals(credential.getClientSecret())) {
            throw new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " 客户端密钥错误");
        }
    }
}