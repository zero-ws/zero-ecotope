package io.zerows.plugins.security.oauth2.server.token;

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
import io.zerows.plugins.oauth2.metadata.OAuth2Credential;
import io.zerows.plugins.oauth2.metadata.OAuth2Security;
import io.zerows.plugins.security.jwt.JwtToken;
import io.zerows.plugins.security.oauth2.server.service.OAuthTool;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public abstract class GranterBase implements Granter {

    protected final JwtToken token;
    protected final OAuth2Security security;

    GranterBase() {
        this.token = JwtToken.of();
        this.security = OAuth2ServerActor.securityOf();
    }

    // =========================================================================
    // 模版方法 (Template Method) - 定义核心流程
    // =========================================================================
    @Override
    public Future<JsonObject> grantAsync(final JsonObject request) {
        // 1. 提取客户端凭证 (Authorization Header / Body)
        final OAuth2Credential credential = this.extractCredential(request);
        if (credential == null || !credential.isValid()) {
            return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + "身份识别失败，缺失客户端 ID。"));
        }

        // 2. 获取并校验客户端 (通用逻辑)
        return this.fetchAndAuthenticateClient(credential)
            .compose(client -> {
                // 3. 【抽象钩子】执行特定模式的逻辑校验，并返回协商后的 Scope
                //    例如：Client Credentials 模式只协商 Scope
                //    例如：Auth Code 模式需要校验 code 是否有效，并从 code 中取出 Scope
                return this.validateGrant(client, request)
                    .compose(finalScope ->
                        // 4. 生成 Token 并持久化
                        this.generateAndStoreResponse(client, finalScope)
                    );
            });
    }

    // =========================================================================
    // 抽象钩子 (Abstract Hooks) - 由子类实现
    // =========================================================================

    /**
     * 特定授权模式的校验逻辑
     *
     * @param client  已通过认证的客户端
     * @param request 请求参数
     * @return Future<String> 最终协商确定的 Scope
     */
    protected abstract Future<String> validateGrant(Oauth2RegisteredClient client, JsonObject request);


    // =========================================================================
    // 公共实现 (Common Implementation)
    // =========================================================================

    private OAuth2Credential extractCredential(final JsonObject request) {
        final String auth = request.getString("Authorization");
        if (StrUtil.isNotEmpty(auth)) {
            return OAuthTool.fromHeader(auth);
        }
        return new OAuth2Credential(
            request.getString(OAuth2Constant.CLIENT_ID),
            request.getString(OAuth2Constant.CLIENT_SECRET)
        );
    }

    private Future<Oauth2RegisteredClient> fetchAndAuthenticateClient(final OAuth2Credential credential) {
        return DB.on(Oauth2RegisteredClientDao.class)
            .<Oauth2RegisteredClient>fetchOneAsync(OAuth2Constant.Field.CLIENT_ID, credential.getClientId())
            .compose(client -> {
                // A. 存在性校验
                if (client == null) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " 客户端不存在: " + credential.getClientId()));
                }
                // B. 模式许可校验
                if (!this.checkGrantTypeSupported(client)) {
                    return Future.failedFuture(new _400BadRequestException(OAuth2Constant.K_PREFIX + " 该客户端未授权使用模式: " + this.grantType().getValue()));
                }
                // C. 密钥校验 (Public Client 除外，但在 Client Credential 模式下通常必须校验)
                // 注意：这里做的是简单的明文比对，生产环境建议使用 PasswordEncoder
                if (StrUtil.isNotEmpty(client.getClientSecret()) &&
                    !client.getClientSecret().equals(credential.getClientSecret())) {
                    return Future.failedFuture(new _401UnauthorizedException(OAuth2Constant.K_PREFIX + " 客户端密钥错误"));
                }
                return Future.succeededFuture(client);
            });
    }

    private boolean checkGrantTypeSupported(final Oauth2RegisteredClient client) {
        final String grants = client.getAuthorizationGrantTypes();
        return StrUtil.isNotEmpty(grants) && Arrays.asList(grants.split(",")).contains(this.grantType().getValue());
    }

    protected String negotiateScope(final String registeredScope, final String requestedScope) {
        if (StrUtil.isEmpty(requestedScope)) {
            return registeredScope;
        }
        if (StrUtil.isEmpty(registeredScope)) {
            return "";
        }
        final Set<String> regSet = new HashSet<>(Arrays.asList(registeredScope.split(",")));
        return Arrays.stream(requestedScope.split(" "))
            .filter(regSet::contains)
            .collect(Collectors.joining(" "));
    }

    private Future<JsonObject> generateAndStoreResponse(final Oauth2RegisteredClient client, final String finalScope) {
        final String jti = Ut.randomString(32);

        // 解析时间
        final Duration duration = R2MO.toDuration(this.security.getAccessTokenAt());
        final long validitySeconds = (duration != null) ? duration.getSeconds() : 3600;

        final Instant nowInstant = Instant.now();
        final Instant expInstant = nowInstant.plusSeconds(validitySeconds);

        final LocalDateTime issuedAt = LocalDateTime.ofInstant(nowInstant, ZoneId.systemDefault());
        final LocalDateTime expiresAt = LocalDateTime.ofInstant(expInstant, ZoneId.systemDefault());

        // JWT Payload
        final JsonObject payload = new JsonObject()
            .put(OAuth2Constant.ISS, this.security.getIssuer())
            .put(OAuth2Constant.SUB, client.getClientId()) // 注意：对于 Auth Code 模式，这里应该是 UserId
            .put(OAuth2Constant.CLIENT_ID, client.getClientId())
            .put(OAuth2Constant.SCOPE, finalScope)
            .put(OAuth2Constant.IAT, nowInstant.getEpochSecond())
            .put(OAuth2Constant.EXP, expInstant.getEpochSecond())
            .put(OAuth2Constant.JTI, jti);

        final String accessToken = this.token.encode(payload);
        final String tokenType = this.security.getTokenType();

        // Database Entity
        final Oauth2Authorization authorization = new Oauth2Authorization();
        authorization.setId(UUID.randomUUID().toString());
        authorization.setRegisteredClientId(client.getClientId());
        authorization.setPrincipalName(client.getClientId()); // TODO: 应该从 validateGrant 返回 Context 中获取实际 Principal
        authorization.setAuthorizationGrantType(this.grantType().getValue());
        authorization.setAuthorizedScopes(finalScope);

        authorization.setAccessTokenValue(accessToken);
        authorization.setAccessTokenIssuedAt(issuedAt);
        authorization.setAccessTokenExpiresAt(expiresAt);
        authorization.setAccessTokenType(tokenType);
        authorization.setAccessTokenScopes(finalScope);

        authorization.setAttributes(new JsonObject()
            .put(OAuth2Constant.JTI, jti)
            .put(OAuth2Constant.ISS, this.security.getIssuer())
            .encode()
        );

        return DB.on(Oauth2AuthorizationDao.class).insertAsync(authorization)
            .map(inserted -> {
                log.info("{} [{}] 签发成功 Client={}, Scope={}", OAuth2Constant.K_PREFIX,
                    this.grantType().getValue(), client.getClientId(), finalScope);
                return new JsonObject()
                    .put(OAuth2Constant.ACCESS_TOKEN, accessToken)
                    .put(OAuth2Constant.TOKEN_TYPE, tokenType)
                    .put(OAuth2Constant.EXPIRES_IN, validitySeconds)
                    .put(OAuth2Constant.SCOPE, finalScope);
            });
    }
}