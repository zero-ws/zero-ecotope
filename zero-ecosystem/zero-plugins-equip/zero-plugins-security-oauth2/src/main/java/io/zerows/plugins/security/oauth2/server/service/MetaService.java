package io.zerows.plugins.security.oauth2.server.service;

import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.OAuth2ServerActor;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2AuthorizationDao;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2Authorization;
import io.zerows.plugins.security.jwt.JwtToken;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class MetaService implements MetaStub {
    private final JwtToken jwtToken;

    public MetaService() {
        this.jwtToken = JwtToken.of();
    }

    @Override
    public Future<JsonObject> jwksAsync() {
        final JsonObject keystoreJ = OAuth2ServerActor.keystoreOf();
        return Future.succeededFuture(keystoreJ);
    }

    @Override
    public Future<JsonObject> userinfoAsync(final String accessToken) {
        // 1. 直接查库验证 Token 有效性
        // 注意：fetchOneAsync 第一个参数是 POJO 属性名 (CamelCase)，不是数据库字段名
        return DB.on(Oauth2AuthorizationDao.class)
            .<Oauth2Authorization>fetchOneAsync("accessTokenValue", accessToken)
            .compose(auth -> {
                // A. 基础校验 (存在性 & 过期)
                if (auth == null) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + "无效的令牌"));
                }

                final LocalDateTime expiresAt = auth.getAccessTokenExpiresAt();
                if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + "令牌已过期"));
                }

                // B. 构建 UserInfo 响应
                final JsonObject userInfo = new JsonObject();
                // 1) sub: 核心用户标识
                userInfo.put(OAuth2Constant.SUB, auth.getPrincipalName());

                // 2) name: 显示名
                userInfo.put("name", auth.getPrincipalName());

                // 3) 尝试从 JWT Payload 中补充 Claims (如 iss)
                try {
                    final JsonObject payload = this.jwtToken.decode(accessToken);
                    if (payload != null && payload.containsKey(OAuth2Constant.ISS)) {
                        userInfo.put(OAuth2Constant.ISS, payload.getValue(OAuth2Constant.ISS));
                    }
                } catch (final Exception ignored) {
                    // 解码失败不阻断核心流程
                }

                log.info("{} 获取成功: {}", OAuth2Constant.K_PREFIX, auth.getPrincipalName());
                return Future.succeededFuture(userInfo);
            });
    }
}
