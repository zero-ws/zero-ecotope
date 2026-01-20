package io.zerows.plugins.oauth2.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * OAuth2 安全策略配置类 (Atom Layer)
 * <p>
 * 对应 YAML 中的 `security.oauth2` 节点。
 * 负责定义认证服务器的"游戏规则"（生命周期、模式、密钥策略）。
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2Security implements Serializable {

    /* =========================================================
     * 1. 基础控制开关
     * ========================================================= */

    /**
     * 是否启用 OAuth2 安全模块
     * YAML: enabled: true
     */
    @JsonProperty("enabled")
    private boolean enabled = false;

    /**
     * 运行模式
     * YAML: mode: JWT
     */
    @JsonProperty("mode")
    private String mode = "JWT";

    /**
     * 发行者标识 (Issuer)
     * YAML: issuer: "<a href="https://auth.r2mo.io">...</a>"
     */
    @JsonProperty("issuer")
    private String issuer = "https://www.zerows.io";

    /**
     * 令牌类型
     */
    @JsonProperty("token-type")
    private String tokenType = "Bearer";

    /**
     * 是否启用资源服务器保护
     * YAML: resourceEnabled: true
     */
    @JsonProperty("resourceEnabled")
    private boolean resourceEnabled = true;

    /* =========================================================
     * 2. 生命周期与过期策略
     * 注意：这里使用 String 接收 (如 "30m"), 后续在 Policy 中解析为 Duration
     * ========================================================= */

    /**
     * 授权码过期时间
     * YAML: authorizationCodeAt: 5m
     */
    @JsonProperty("authorizationCodeAt")
    private String authorizationCodeAt = "5m";

    /**
     * 访问令牌过期时间
     * YAML: accessTokenAt: 2h
     */
    @JsonProperty("accessTokenAt")
    private String accessTokenAt = "30m";

    /**
     * 刷新令牌过期时间
     * YAML: refreshTokenAt: 30d
     */
    @JsonProperty("refreshTokenAt")
    private String refreshTokenAt = "7d";

    /**
     * 设备码过期时间
     * YAML: deviceCodeAt: 15m
     */
    @JsonProperty("deviceCodeAt")
    private String deviceCodeAt = "30m";

    /**
     * 时间偏差 (秒)
     * YAML: skew: 60
     */
    @JsonProperty("skew")
    private long skew = 60;

    /* =========================================================
     * 3. 令牌行为策略
     * ========================================================= */

    /**
     * 是否重用刷新令牌
     * YAML: reuseRefreshToken: false
     */
    @JsonProperty("reuseRefreshToken")
    private boolean reuseRefreshToken = true;

    /* =========================================================
     * 4. 密钥管理 (嵌套对象)
     * ========================================================= */

    /**
     * 密钥库配置
     * YAML: keyStore: { type: ..., path: ... }
     */
    @JsonProperty("keyStore")
    private KeyStoreConfig keyStore = new KeyStoreConfig();

    /* =========================================================
     * 5. OIDC 配置 (嵌套对象)
     * ========================================================= */

    /**
     * OIDC 相关配置
     * YAML: oidc: { userClaims: true }
     */
    @JsonProperty("oidc")
    private OidcConfig oidc = new OidcConfig();

    /* =========================================================
     * 6. 预置客户端列表
     * ========================================================= */

    /**
     * 预置客户端 ID 列表
     * YAML: clients: [ "id1", "id2" ] 或 clients: []
     * 映射关系: YAML 数组 -> Java List<String>
     */
    @JsonProperty("clients")
    private List<String> clients = new ArrayList<>();


    /* ---------------------------------------------------------
     * 内部类定义
     * --------------------------------------------------------- */

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OidcConfig implements Serializable {
        @JsonProperty("userClaims")
        private boolean userClaims = true;
    }

    @Data
    @Accessors(chain = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KeyStoreConfig implements Serializable {
        @JsonProperty("type")
        private String type = "jks";

        @JsonProperty("path")
        private String path = "keystore/oauth2.jks";

        @JsonProperty("password")
        private String password;

        @JsonProperty("alias")
        private String alias;
    }
}