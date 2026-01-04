package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Redis 配置 POJO
 * <p>
 * 1. 修复 NOAUTH 问题：直接暴露 password 字段，供 RedisOptions 直接读取。
 * 2. 安全性提升：connectionString 中不再拼接密码，防止日志泄露。
 * </p>
 *
 * @author lang : 2025-10-06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YmRedis implements Serializable {

    // =========================================================
    // 基础连接字段
    // =========================================================

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String host = "127.0.0.1";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int port = 6379;

    // 🌟 重点修改：移除 WRITE_ONLY
    // 让 Jackson 在序列化时包含此字段，这样 new RedisOptions(json) 能直接读到密码
    // 而不需要去解析 connectionString
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer database = 0;

    /**
     * 如果配置了 endpoint (例如 redis://...)，则将其作为 connectionString 的基础
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String endpoint;


    // =========================================================
    // Vert.x 直接需要的字段 (Java <-> Json)
    // =========================================================

    private String type;
    private String role = "MASTER";
    private String masterName;

    private Integer maxPoolSize = 32;
    private Integer maxWaitingHandlers = 1024;
    private Long poolRecycleTimeout = 15000L;
    private Integer maxReconnectAttempts = 5;
    private Long reconnectInterval = 1000L;

    @JsonProperty("netClientOptions")
    private YmNet config = new YmNet();


    // =========================================================
    // 核心：计算字段
    // =========================================================

    /**
     * 虚拟 Getter：生成 "connectionString"
     * 策略调整：仅生成 "redis://host:port/db"，不包含密码！
     * 密码通过上面的 password 字段独立传递。
     */
    @JsonProperty("connectionString")
    public String getComputedConnectionString() {
        if ("CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        return this.resolveUri(false); // 传入 false，不包含密码
    }

    /**
     * 虚拟 Getter：生成 "endpoints" 数组 (Cluster 模式)
     */
    @JsonProperty("endpoints")
    public List<String> getComputedEndpoints() {
        if (!"CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        // Cluster 模式下，通常 endpoints 列表只是地址，密码也是统一配置的
        final String raw = this.resolveUri(false);
        if (raw == null) {
            return null;
        }
        return Arrays.stream(raw.split(","))
            .map(String::trim)
            .collect(Collectors.toList());
    }


    // =========================================================
    // 内部逻辑 Helper
    // =========================================================

    @JsonIgnore
    private String resolveUri(final boolean includePassword) {
        // 1. 优先使用显式 endpoint
        if (Objects.nonNull(this.endpoint) && !this.endpoint.isBlank()) {
            return this.endpoint;
        }

        // 2. 自动组装
        final StringBuilder uri = new StringBuilder("redis://");

        // 🌟 策略调整：只有明确要求包含密码时才拼接
        // 既然我们已经暴露了 password 字段，通常这里就不需要拼接了，避免特殊字符解析错误
        if (includePassword && Objects.nonNull(this.password) && !this.password.isBlank()) {
            // 注意：如果密码包含 @ 等字符，拼接在 URL 里需要 URLEncode，
            // 既然我们要避免解析，这里直接不拼是最好的。
            uri.append(":").append(this.password).append("@");
        }

        uri.append(this.host).append(":").append(this.port);

        if (Objects.nonNull(this.database)) {
            uri.append("/").append(this.database);
        }
        return uri.toString();
    }

    // =========================================================
    // 内部类：网络配置
    // =========================================================
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class YmNet implements Serializable {
        private Integer connectTimeout = 10000;
        private Boolean tcpKeepAlive = true;
        private Boolean tcpNoDelay = true;
        private Boolean ssl = false;
        private Boolean trustAll = true;
        private String hostnameVerificationAlgorithm = "";
    }
}