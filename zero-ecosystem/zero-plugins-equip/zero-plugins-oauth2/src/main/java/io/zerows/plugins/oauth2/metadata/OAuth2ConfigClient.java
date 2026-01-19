package io.zerows.plugins.oauth2.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * OAuth2 客户端配置模型 (DTO)
 * <p>
 * 该类用于接收 YAML/JSON 配置文件中的数据，并与数据库实体 `Oauth2RegisteredClient` 对应。
 * 使用 @JsonProperty 确保能正确解析 kebab-case (client-id) 格式的配置。
 * </p>
 *
 * @author lang
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2ConfigClient implements Serializable {

    /* =========================================================
     * 1. 基础标识与密钥
     * ========================================================= */

    /**
     * 主键 ID (对应 DB: ID)
     * <p>配置文件通常不写此项，由系统生成，但保留字段以支持全属性映射</p>
     */
    @JsonProperty("id")
    private String id;

    /**
     * 客户端 ID (对应 YAML: client-id)
     * 对应 DB: CLIENT_ID
     */
    @JsonProperty("client-id")
    private String clientId;

    /**
     * 客户端 ID 签发时间 (对应 DB: CLIENT_ID_ISSUED_AT)
     */
    @JsonProperty("client-id-issued-at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clientIdIssuedAt;

    /**
     * 客户端密钥 (对应 YAML: client-secret)
     * 对应 DB: CLIENT_SECRET
     */
    @JsonProperty("client-secret")
    private String clientSecret;

    /**
     * 客户端密钥过期时间 (对应 DB: CLIENT_SECRET_EXPIRES_AT)
     */
    @JsonProperty("client-secret-expires-at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clientSecretExpiresAt;

    /**
     * 客户端显示名称 (对应 YAML: client-name)
     * 对应 DB: CLIENT_NAME
     */
    @JsonProperty("client-name")
    private String clientName;

    /* =========================================================
     * 2. 认证与授权模式
     * ========================================================= */

    /**
     * 客户端认证方式 (对应 YAML: client-authentication-method)
     * <p>对应 DB: CLIENT_AUTHENTICATION_METHODS (Set 转 String)</p>
     */
    @JsonProperty("client-authentication-method")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> clientAuthenticationMethods;

    /**
     * 授权类型 (对应 YAML: authorization-grant-type)
     * <p>对应 DB: AUTHORIZATION_GRANT_TYPES (Set 转 String)</p>
     */
    @JsonProperty("authorization-grant-type")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> authorizationGrantTypes;

    /* =========================================================
     * 3. 重定向与作用域
     * ========================================================= */

    /**
     * 授权回调地址 (对应 YAML: redirect-uri)
     * <p>对应 DB: REDIRECT_URIS (Set 转 String)</p>
     */
    @JsonProperty("redirect-uri")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> redirectUris;

    /**
     * 登出回调地址 (对应 YAML: post-logout-redirect-uri)
     * <p>对应 DB: POST_LOGOUT_REDIRECT_URIS (Set 转 String)</p>
     */
    @JsonProperty("post-logout-redirect-uri")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> postLogoutRedirectUris;

    /**
     * 申请的作用域 (对应 YAML: scope)
     * <p>对应 DB: SCOPES (Set 转 String)</p>
     */
    @JsonProperty("scope")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private Set<String> scopes;

    /* =========================================================
     * 4. 复杂设置对象 (对应 YAML Nested Object)
     * ========================================================= */

    /**
     * 客户端设置 (对应 YAML: client-setting)
     * <p>对应 DB: CLIENT_SETTINGS (存储为 JSON 字符串)</p>
     */
    @JsonProperty("client-setting")
    private ClientSetting clientSettings;

    /**
     * 令牌设置 (对应 YAML: token-setting)
     * <p>对应 DB: TOKEN_SETTINGS (存储为 JSON 字符串)</p>
     */
    @JsonProperty("token-setting")
    private TokenSetting tokenSettings;

    /* =========================================================
     * 5. 多租户与扩展字段
     * ========================================================= */

    /**
     * 提供方标识 (对应 YAML: provider)
     * <p>注意：此字段不在 Oauth2RegisteredClient 表中，但属于 Registration 配置的一部分</p>
     */
    @JsonProperty("provider")
    private String provider;

    /**
     * 租户 ID (对应 DB: TENANT_ID)
     */
    @JsonProperty("tenant-id")
    private String tenantId;

    /**
     * 应用 ID (对应 DB: APP_ID)
     */
    @JsonProperty("app-id")
    private String appId;

    /**
     * 扩展字段 (对应 DB: EXT)
     */
    @JsonProperty("ext")
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject ext = new JsonObject();


    /* =========================================================
     * 内部类定义 (用于解析 YAML 中的嵌套对象)
     * ========================================================= */

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientSetting implements Serializable {

        /**
         * 是否需要 Proof Key (PKCE)
         */
        @JsonProperty("require-proof-key")
        private Boolean requireProofKey = false;

        /**
         * 是否需要授权确认页面 (Consent)
         */
        @JsonProperty("require-authorization-consent")
        private Boolean requireAuthorizationConsent = false;

        /**
         * JWK Set 地址
         */
        @JsonProperty("jwk-set-uri")
        private String jwkSetUri;

        /**
         * 签名算法
         */
        @JsonProperty("signing-algorithm")
        private String signingAlgorithm;

        /**
         * 转换为 Vert.x JsonObject
         */
        public JsonObject toJson() {
            return JsonObject.mapFrom(this);
        }
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenSetting implements Serializable {

        /**
         * Access Token 有效期 (秒)
         */
        @JsonProperty("access-token-time-to-live")
        private Long accessTokenTimeToLive;

        /**
         * Refresh Token 有效期 (秒)
         */
        @JsonProperty("refresh-token-time-to-live")
        private Long refreshTokenTimeToLive;

        /**
         * 是否复用 Refresh Token
         */
        @JsonProperty("reuse-refresh-tokens")
        private Boolean reuseRefreshTokens = true;

        /**
         * 是否启用 ID Token
         */
        @JsonProperty("enable-id-token")
        private Boolean enableIdToken = true;

        /**
         * 转换为 Vert.x JsonObject
         */
        public JsonObject toJson() {
            return JsonObject.mapFrom(this);
        }
    }
}