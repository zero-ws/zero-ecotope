package io.zerows.plugins.security.metadata;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.base.util.R2MO;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.security.SecurityConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <pre>
 *     security:                {@link YmSecurity}
 *       # 授权部分 ---------------
 *       authorization:
 *         enabled: true
 *       # 认证部分 ---------------
 *       wall: /api
 *       config:
 *         master:
 *           type: jwt
 *       limit:
 *         session:
 *         token:
 *         timeout:
 *         types:
 *         - JWT:4096:30m
 *       scope:
 *         app: true
 *         tenant: true
 *       captcha:
 *         type: circle
 *         expiredAt: 30s
 *         width: 150
 *         height: 50
 *         textAlpha: 0.8
 *         code:
 *           type: RANDOM
 *           length: 4
 *         font:
 *           name: Arial
 *           weight: 0
 *           size: 20
 *       jwt:
 *         options:
 *         provider:
 *       basic:
 *         options:
 *       digest:
 *         options:
 *         provider:
 *       oauth2:
 *         options:
 *         provider:
 * </pre>
 *
 * @author lang : 2025-12-30
 */
@Data
@Slf4j
public class YmSecurity implements Serializable {
    private String wall = "/api";
    private ConcurrentMap<String, JsonObject> config = new ConcurrentHashMap<>();
    private Limit limit = new Limit();
    private Scope scope = new Scope();
    private YmSecurityCaptcha captcha;
    private YmSecurityAuthorization authorization = new YmSecurityAuthorization();
    // 2. 核心目标：用来存储不同类型的安全配置
    // 注意：这里加 @JsonIgnore 是为了防止默认序列化行为冲突，我们将通过 getter/setter 或 AnyGetter 处理
    @JsonIgnore
    private ConcurrentMap<String, SecurityConfig> extension = new ConcurrentHashMap<>();

    @JsonAnySetter
    public void setExtension(final String type, final Object value) {
        // 转换
        if (Objects.nonNull(type) && value instanceof Map<?, ?>) {
            final SecurityConfig configuration = new SecurityConfig(type, JsonObject.mapFrom(value));
            this.extension.put(type, configuration);
        }
    }

    public boolean isCaptcha() {
        return Objects.nonNull(this.captcha) && this.captcha.isEnabled();
    }

    public boolean isAuthorization() {
        return Objects.nonNull(this.authorization) && this.authorization.isEnabled();
    }

    public SecurityConfig extension(final String type) {
        return this.extension.getOrDefault(type, null);
    }

    @Data
    public static class Scope implements Serializable {
        private boolean app = false;
        private boolean tenant = false;
    }

    @Data
    public static class Limit implements Serializable {
        /**
         * <pre>
         *     - 不登录的 BASIC，一次性的，只能使用 {@link TokenType#BASIC}，无刷新机制，因为客户端拿不到 accessToken
         *     - 登录后的 BASIC，只能使用 {@link TokenType#AES}，支持刷新机制
         *     - 开启了 JWT 和 OAUTH2 之后
         *       - POST /auth/login             依旧使用 AES，BASIC 标配
         *       - POST /auth/login-jwt         使用 JWT，JWT 标配
         *       - OAUTH2 登录后可使用 {@link TokenType#JWT} 或 {@link TokenType#OPAQUE}，取决于授权服务器配置
         *     其他场景：短信、邮件、LDAP 则使用此字段定义的默认值
         *       - 这种场景下可以使用 tokenType 定义的类型，此时才能选择 {@link TokenType#OPAQUE}
         * </pre>
         */
        @JsonProperty("token-type")
        private TokenType tokenType = TokenType.JWT;
        // Session 数量
        private long session = 8192;
        // Token 数量
        private long token = 4096;
        // 验证码数量
        private long authorize = 2048;
        /*
         * Session 超时时间（UserContext,UserAt,UserVector）
         * Token 令牌超时时间
         */
        private String expiredAt = "120m";
        private String refreshAt = "7d";
        private List<String> types = new ArrayList<>();

        public Duration expiredAt() {
            return R2MO.toDuration(this.expiredAt);
        }

        public Duration refreshAt() {
            return R2MO.toDuration(this.refreshAt);
        }
    }
}
