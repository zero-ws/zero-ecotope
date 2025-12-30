package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     vertx:
 *       security:
 *         user:
 *           name:
 *           password:
 *           roles:
 *         limit:
 *           session:
 *           token:
 *           timeout:
 *           types:
 *           - JWT:4096:30m
 *         scope:
 *           app: true
 *           tenant: true
 *         captcha:
 *         captcha-email:
 *           length:
 *           expiredAt:
 *           subject:
 *         captcha-sms:
 *           length:
 *           expiredAt:
 *           template:
 *         # 认证方式部分采用如下结构
 *         jwt:
 *           options:
 *         basic:
 *           options:
 *         digest:
 *           options:
 *         oauth2:
 *           options:
 * </pre>
 *
 * @author lang : 2025-10-05
 */
@Data
public class YmSecurity implements Serializable {
    private Scope scope = new Scope();
    private Limit limit = new Limit();


    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    @JsonProperty("captcha-sms")
    private JsonObject captchaSms;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    @JsonProperty("captcha-email")
    private JsonObject captchaEmail;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject captcha;

    private Jwt jwt;
    private User user;
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject config = new JsonObject();

    /**
     * @author lang : 2025-10-28
     */
    @Data
    public static class Scope implements Serializable {
        private Boolean app = Boolean.FALSE;
        private Boolean tenant = Boolean.FALSE;
    }

    @Data
    public static class Limit implements Serializable {
        private Integer session;
        private Integer token;
        private Integer timeout;
        private List<String> types = Collections.emptyList();
    }

    /**
     * @author lang : 2025-10-05
     */
    @Data
    public static class Jwt implements Serializable {
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options = new JsonObject();
    }

    /**
     * @author lang : 2025-10-28
     */
    @Data
    public static class User implements Serializable {
        private String name;
        private String password;
        private String roles;

        public List<String> roles() {
            if (Objects.isNull(this.roles)) {
                return Collections.emptyList();
            }
            return Arrays.asList(this.roles.split(","));
        }
    }
}
