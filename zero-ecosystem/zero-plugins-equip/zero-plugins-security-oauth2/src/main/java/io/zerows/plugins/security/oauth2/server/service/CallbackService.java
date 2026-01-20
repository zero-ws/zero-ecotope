package io.zerows.plugins.security.oauth2.server.service;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.zerows.cosmic.plugins.client.RestClient;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.plugins.oauth2.OAuth2ClientActor;
import io.zerows.plugins.oauth2.domain.tables.daos.Oauth2RegisteredClientDao;
import io.zerows.plugins.oauth2.domain.tables.pojos.Oauth2RegisteredClient;
import io.zerows.plugins.oauth2.metadata.OAuth2ConfigProvider;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Slf4j
public class CallbackService implements CallbackStub {

    // 常量定义
    private static final String STATE_SEPARATOR = "_VC_";
    private static final String GRANT_TYPE_CODE = "authorization_code";

    @Inject
    private RestClient restClient;

    public CallbackService() {
    }

    @Override
    public Future<JsonObject> backAsync(final JsonObject request) {
        // 1. 从输入提取参数
        final String registrationId = request.getString("clientId");
        final String code = request.getString("code");
        final String state = request.getString("state");
        final String error = request.getString("error");

        // 2. 基础校验
        if (error != null && !error.isEmpty()) {
            return Future.failedFuture(new RuntimeException("[ OAuth2 ] 授权服务端返回错误: " + error));
        }
        if (code == null || code.isEmpty()) {
            return Future.failedFuture(new RuntimeException("[ OAuth2 ] 授权码 code 不能为空"));
        }
        if (registrationId == null || registrationId.isEmpty()) {
            return Future.failedFuture(new RuntimeException("[ OAuth2 ] Client ID (registrationId) 不能为空"));
        }

        // 3. 异步流程：先查库，再换 Token
        return this.fetchClient(registrationId)
            .compose(client -> {
                // 3.1 校验 Client 是否存在
                if (Objects.isNull(client)) {
                    return Future.failedFuture(new RuntimeException("[ OAuth2 ] 未找到注册的 Client 配置: " + registrationId));
                }
                return this.exchangeToken(client, code, state);
            })
            .recover(ex -> {
                // 统一异常包装
                return Future.failedFuture(new RuntimeException("[ OAuth2 ] 流程失败: " + ex.getMessage(), ex));
            });
    }

    /**
     * 核心逻辑：执行 Token 交换
     */
    private Future<JsonObject> exchangeToken(final Oauth2RegisteredClient client, final String code, final String state) {
        // 1. 解析配置信息
        final String clientId = client.getClientId();
        final String clientSecret = client.getClientSecret();

        // 解析 Redirect URI
        final String redirectUri = this.resolveRedirectUri(client);

        // 解析 Token Endpoint (从 Provider 配置中获取)
        final String tokenUrl = this.resolveTokenEndpoint(client);

        // 2. PKCE 处理
        String codeVerifier = null;
        if (state != null && state.contains(STATE_SEPARATOR)) {
            final String[] parts = state.split(STATE_SEPARATOR);
            if (parts.length > 1) {
                codeVerifier = parts[parts.length - 1];
            }
        }

        // 3. 构造请求头 (Basic Auth)
        final MultiMap headers = MultiMap.caseInsensitiveMultiMap();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // 如果有 Secret，使用 Basic Auth (推荐方式)
        if (clientSecret != null && !clientSecret.isEmpty()) {
            final String auth = clientId + ":" + clientSecret;
            // 必须先转为 UTF-8 字节，再进行 Base64 编码
            final String encodedAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
        }

        // 4. 构造表单参数
        final JsonObject formParams = new JsonObject()
            .put("grant_type", GRANT_TYPE_CODE)
            .put("code", code)
            .put("redirect_uri", redirectUri);

        // Public Client 兼容 (无 Secret 时，Client ID 放 Body)
        if (clientSecret == null || clientSecret.isEmpty()) {
            formParams.put("client_id", clientId);
        }

        if (codeVerifier != null) {
            formParams.put("code_verifier", codeVerifier);
        }

        // 5. 发送请求
        return this.restClient.doPostForm(tokenUrl, formParams, headers)
            .compose(Future::succeededFuture);
    }

    // =================================================================================
    // 辅助方法
    // =================================================================================

    /**
     * 数据库查询：根据 Client ID 获取配置
     */
    private Future<Oauth2RegisteredClient> fetchClient(final String clientId) {
        return DB.on(Oauth2RegisteredClientDao.class).fetchOneAsync("clientId", clientId);
    }

    private String resolveRedirectUri(final Oauth2RegisteredClient client) {
        final String uris = client.getRedirectUris();
        if (uris != null && !uris.isEmpty()) {
            return uris.split(",")[0].trim();
        }

        log.warn("[ OAuth2 ] Client {} 未配置 Redirect URI，使用默认值", client.getClientId());
        return "http://localhost:6083/oauth2/authorized/" + client.getClientId();
    }

    /**
     * 解析 Token 端点
     * <p>优先读取 YAML 配置中的 provider -> token-uri</p>
     */
    private String resolveTokenEndpoint(final Oauth2RegisteredClient client) {
        // 1. 通过 ClientId 查找关联的 Provider 配置
        final OAuth2ConfigProvider provider = OAuth2ClientActor.findProvider(client.getClientId());

        // 2. 如果配置存在且 tokenUri 有效，直接返回
        if (Objects.nonNull(provider)
            && Objects.nonNull(provider.getTokenUri())
            && !provider.getTokenUri().isEmpty()) {
            return provider.getTokenUri();
        }

        log.warn("[ OAuth2 ] Client {} 未配置有效的 Token Endpoint，默认值依赖 rest: 节点的配置！！", client.getClientId());
        // 3. 兜底默认值 (针对内部自建 OAuth Server)
        return "/oauth/token";
    }
}