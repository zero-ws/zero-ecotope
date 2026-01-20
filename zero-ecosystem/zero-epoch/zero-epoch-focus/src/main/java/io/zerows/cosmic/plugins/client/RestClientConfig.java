package io.zerows.cosmic.plugins.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * REST 客户端专用配置类
 * <p>
 * 对应 YAML 配置节点：rest
 * 提供默认值支持，可直接通过 JsonObject.mapTo(RestConfig.class) 转换
 */
@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestClientConfig implements Serializable {

    /**
     * 是否启用
     */
    @JsonProperty("enabled")
    private Boolean enabled = true;

    /**
     * 默认主机地址
     * <p>当调用 doGet("/path") 等相对路径时使用</p>
     */
    @JsonProperty("host")
    private String host;

    /**
     * 默认端口
     * <p>默认 http:80, https:443</p>
     */
    @JsonProperty("port")
    private Integer port;

    /**
     * 是否开启 SSL/TLS
     * <p>默认: false</p>
     */
    @JsonProperty("ssl")
    private Boolean ssl = false;

    /**
     * 是否信任所有证书
     * <p>仅在 ssl: true 时有效，生产环境建议 false</p>
     */
    @JsonProperty("trustAll")
    private Boolean trustAll = false;

    /**
     * 自定义 User-Agent 头
     * <p>默认: ZeroWS-RestClient/1.0</p>
     */
    @JsonProperty("userAgent")
    private String userAgent = "ZeroWS-RestClient/1.0";

    /**
     * 是否保持长连接
     * <p>推荐开启以复用 TCP 连接，默认: true</p>
     */
    @JsonProperty("keepAlive")
    private Boolean keepAlive = true;

    /**
     * 连接超时时间 (单位: 毫秒)
     * <p>默认: 5000ms</p>
     */
    @JsonProperty("connectTimeout")
    private Integer connectTimeout = 5000;

    /**
     * 空闲连接回收时间 (单位: 秒)
     * <p>默认: 60s</p>
     */
    @JsonProperty("idleTimeout")
    private Integer idleTimeout = 60;

    /**
     * 最大连接池大小
     * <p>默认: 20</p>
     */
    @JsonProperty("maxPoolSize")
    private Integer maxPoolSize = 20;

    /**
     * 辅助方法：从 Vert.x JsonObject 创建
     */
    public static RestClientConfig fromJson(final JsonObject json) {
        if (json == null) {
            return new RestClientConfig();
        }
        return json.mapTo(RestClientConfig.class);
    }

    /**
     * 辅助方法：转换为 Vert.x JsonObject
     */
    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}