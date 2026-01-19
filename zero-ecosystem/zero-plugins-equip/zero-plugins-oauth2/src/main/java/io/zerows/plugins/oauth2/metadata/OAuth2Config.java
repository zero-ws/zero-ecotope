package io.zerows.plugins.oauth2.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * OAuth2 总配置入口 (DTO)
 * <p>
 * 对应 YAML 中的根节点 `oauth2` 下的结构。
 * 包含两个核心 Map：
 * 1. registration: 客户端注册信息 (Key: registrationId)
 * 2. provider: 提供方配置信息 (Key: providerId)
 * </p>
 *
 * @author lang
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OAuth2Config implements Serializable {

    private String endpoint;
    /**
     * 客户端注册配置集合 (对应 YAML: oauth2.registration)
     * <p>Key: 注册ID (如 r2mo-desktop-app)</p>
     * <p>Value: 具体的客户端配置</p>
     */
    @JsonProperty("registration")
    private Map<String, OAuth2ConfigClient> registration;

    /**
     * 提供方配置集合 (对应 YAML: oauth2.provider)
     * <p>Key: 提供方ID (如 r2mo-provider)</p>
     * <p>Value: 具体的提供方端点配置</p>
     */
    @JsonProperty("provider")
    private Map<String, OAuth2ConfigProvider> provider;

    /**
     * 转换为 Vert.x JsonObject
     */
    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}