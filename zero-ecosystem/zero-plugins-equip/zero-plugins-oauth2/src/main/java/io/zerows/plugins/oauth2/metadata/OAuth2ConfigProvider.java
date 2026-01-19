package io.zerows.plugins.oauth2.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * OAuth2 提供方配置模型 (DTO)
 * <p>
 * 用于映射 YAML 中 `provider` 节点的配置信息。
 * 记录了用于对接外部身份源（如 Keycloak, Google, WeCom）的端点地址。
 * </p>
 *
 * @author lang
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2ConfigProvider implements Serializable {

    /**
     * 发行者 URI (对应 YAML: issuer-uri)
     * <p>如果配置了此项，系统通常会自动发现其他端点 (.well-known/openid-configuration)</p>
     */
    @JsonProperty("issuer-uri")
    private String issuerUri;

    /**
     * 授权端点 (对应 YAML: authorization-uri)
     * <p>用户浏览器跳转进行登录的地址</p>
     */
    @JsonProperty("authorization-uri")
    private String authorizationUri;

    /**
     * 令牌端点 (对应 YAML: token-uri)
     * <p>后端使用 Code 换取 Access Token 的地址</p>
     */
    @JsonProperty("token-uri")
    private String tokenUri;

    /**
     * 用户信息端点 (对应 YAML: user-info-uri)
     * <p>使用 Access Token 获取用户详细信息的地址</p>
     */
    @JsonProperty("user-info-uri")
    private String userInfoUri;

    /**
     * 用户信息请求认证方式 (对应 YAML: user-info-authentication-method)
     * <p>例如: header, form, query</p>
     */
    @JsonProperty("user-info-authentication-method")
    private String userInfoAuthenticationMethod;

    /**
     * JWK Set 端点 (对应 YAML: jwk-set-uri)
     * <p>用于获取公钥以验证 Token 签名的地址</p>
     */
    @JsonProperty("jwk-set-uri")
    private String jwkSetUri;

    /**
     * 用户名属性名 (对应 YAML: user-name-attribute)
     * <p>指明 UserInfo 响应中哪个字段代表用户名 (如: sub, username, email)</p>
     */
    @JsonProperty("user-name-attribute")
    private String userNameAttribute;

    /**
     * 转换为 Vert.x JsonObject
     */
    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}