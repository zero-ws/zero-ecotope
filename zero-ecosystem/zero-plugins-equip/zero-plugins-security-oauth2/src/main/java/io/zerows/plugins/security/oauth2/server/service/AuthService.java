package io.zerows.plugins.security.oauth2.server.service;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.plugins.oauth2.OAuth2Constant;
import io.zerows.plugins.oauth2.OAuth2ServerActor;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2AuthorizationDao;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2RegisteredClientDao;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2Authorization;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2GrantType;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class AuthService implements AuthStub {

    private final OAuth2Security security;

    public AuthService() {
        // 加载配置，获取 code 的有效期配置 (authorizationCodeAt)
        this.security = OAuth2ServerActor.securityOf();
    }

    @Override
    public Future<String> authorizeAsync(final JsonObject request) {
        // 1. 提取参数
        final String responseType = request.getString(OAuth2Constant.RESPONSE_TYPE);
        final String clientId = request.getString(OAuth2Constant.CLIENT_ID);
        final String redirectUri = request.getString(OAuth2Constant.REDIRECT_URI);
        final String scope = request.getString(OAuth2Constant.SCOPE);
        final String state = request.getString(OAuth2Constant.STATE);

        // 2. 基础参数校验
        if (!"code".equals(responseType)) {
            return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 不支持的 response_type，仅支持 'code'"));
        }
        if (StrUtil.isBlank(clientId)) {
            return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " client_id 不能为空"));
        }

        // 3. 校验客户端信息
        return DB.on(Oauth2RegisteredClientDao.class)
            .<Oauth2RegisteredClient>fetchOneAsync(OAuth2Constant.Field.CLIENT_ID, clientId)
            .compose(client -> {
                // A. 客户端是否存在
                if (client == null) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " 客户端不存在"));
                }

                // B. 是否开启 authorization_code 模式
                if (!this.checkGrantTypeSupported(client)) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " 该客户端未授权 authorization_code 模式"));
                }

                // C. 校验回调地址 (Redirect URI)
                // 安全关键：必须严格匹配数据库配置的白名单
                if (!this.validateRedirectUri(client.getRedirectUris(), redirectUri)) {
                    return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 非法的 redirect_uri: " + redirectUri));
                }

                // 4. 生成 Code 与 计算过期时间
                final String code = Ut.randomString(16); // 生成 16位随机码

                // 从配置读取 authorizationCodeAt (例如 "5m")
                final Duration duration = R2MO.toDuration(this.security.getAuthorizationCodeAt());
                // 默认 5分钟 (300秒)
                final long seconds = (duration != null) ? duration.getSeconds() : 300;

                final Instant now = Instant.now();
                final LocalDateTime issuedAt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
                final LocalDateTime expiresAt = LocalDateTime.ofInstant(now.plusSeconds(seconds), ZoneId.systemDefault());

                // 5. 构建持久化对象 (Oauth2Authorization)
                final Oauth2Authorization authorization = new Oauth2Authorization();
                authorization.setId(UUID.randomUUID().toString());
                authorization.setRegisteredClientId(client.getClientId());

                // ⚠️ 关键点：绑定用户主体
                // 在真实的 Web 场景中，这一步之前会强制 redirect 到登录页，这里模拟已登录用户
                authorization.setPrincipalName("zero-user");

                authorization.setAuthorizationGrantType(OAuth2GrantType.AUTHORIZATION_CODE.getValue());
                authorization.setAuthorizedScopes(scope);

                // 📌 核心：存储 Code 和 过期时间
                authorization.setAuthorizationCodeValue(code);
                authorization.setAuthorizationCodeIssuedAt(issuedAt);
                authorization.setAuthorizationCodeExpiresAt(expiresAt);

                // 📌 核心：存储 redirect_uri (RFC 要求换 Token 时必须二次验证一致性)
                final JsonObject attributes = new JsonObject();
                if (StrUtil.isNotBlank(redirectUri)) {
                    attributes.put(OAuth2Constant.REDIRECT_URI, redirectUri);
                }
                authorization.setAttributes(attributes.encode());
                authorization.setState(state);

                // 6. 落库 (Insert) 并返回结果
                return DB.on(Oauth2AuthorizationDao.class).insertAsync(authorization)
                    .map(inserted -> {
                        log.info("{} 授权码生成成功 Code={}, Expires={}, Client={}",
                            OAuth2Constant.K_PREFIX, code, expiresAt, clientId);

                        // 7. 构造重定向 URL: uri?code=xxx&state=xxx
                        final StringBuilder location = new StringBuilder(redirectUri);
                        location.append(redirectUri.contains("?") ? "&" : "?");
                        location.append("code=").append(code);
                        if (StrUtil.isNotBlank(state)) {
                            location.append("&state=").append(state);
                        }

                        final String loggedUrl = location.toString();
                        log.info("{} 最终重定向：{}", OAuth2Constant.K_PREFIX, loggedUrl);
                        // 返回 JSON 包含重定向地址 (Agent 层决定是直接 302 还是返回 JSON 给前端)
                        return loggedUrl;
                    });
            });
    }

    // -------------------------------------------------------------------------
    // 内部校验辅助
    // -------------------------------------------------------------------------

    private boolean checkGrantTypeSupported(final Oauth2RegisteredClient client) {
        final String grants = client.getAuthorizationGrantTypes();
        return StrUtil.isNotEmpty(grants) && Arrays.asList(grants.split(",")).contains(OAuth2GrantType.AUTHORIZATION_CODE.getValue());
    }

    private boolean validateRedirectUri(final String registeredUris, final String requestedUri) {
        // 如果请求没传 uri，在严格模式下视为非法
        if (StrUtil.isBlank(requestedUri)) {
            return false;
        }
        if (StrUtil.isBlank(registeredUris)) {
            return false;
        }
        // 白名单匹配：数据库可能配置多个，用逗号分隔
        return Arrays.asList(registeredUris.split(",")).contains(requestedUri);
    }
}