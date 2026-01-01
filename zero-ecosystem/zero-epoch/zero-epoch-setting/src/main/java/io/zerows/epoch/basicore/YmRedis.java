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
 * 1. 支持标准 Java Bean 规范（无链式 Setter），确保序列化兼容性。
 * 2. 输入：支持 host/port 等散装字段。
 * 3. 输出：自动计算为 Vert.x RedisOptions 所需的 connectionString 格式。
 * </p>
 *
 * @author lang : 2025-10-06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YmRedis implements Serializable {

    // =========================================================
    // 原始配置字段 (Config -> Java)
    // Access.WRITE_ONLY: 仅用于反序列化（读取配置），序列化成 JsonObject 时隐藏
    // =========================================================

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String host = "127.0.0.1";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int port = 6379;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer database = 0;

    /**
     * 如果配置了 endpoint (例如 redis://...)，则忽略 host/port/password
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String endpoint;


    // =========================================================
    // Vert.x 直接需要的字段 (Java <-> Json)
    // =========================================================

    private String type;

    // Sentinel 模式专用
    private String role = "MASTER";
    private String masterName;

    // 连接池与性能配置
    private Integer maxPoolSize = 6;
    private Integer maxWaitingHandlers = 1024;
    private Long poolRecycleTimeout = 15000L;
    private Integer maxReconnectAttempts = 5;
    private Long reconnectInterval = 1000L;

    // 嵌套网络配置 (映射为 netClientOptions)
    @JsonProperty("netClientOptions")
    private YmNet config = new YmNet();


    // =========================================================
    // 核心：计算字段 (Java -> Json)
    // 序列化时，Jackson 会调用这些 getter 生成 connectionString 或 endpoints
    // =========================================================

    /**
     * 虚拟 Getter：生成 "connectionString"
     * 只有当不是 Cluster 模式时才输出
     */
    @JsonProperty("connectionString")
    public String getComputedConnectionString() {
        if ("CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        return this.resolveUri();
    }

    /**
     * 虚拟 Getter：生成 "endpoints" 数组
     * 只有当是 Cluster 模式时才输出
     */
    @JsonProperty("endpoints")
    public List<String> getComputedEndpoints() {
        if (!"CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        final String raw = this.resolveUri();
        if (raw == null) {
            return null;
        }

        // 假设集群配置 endpoint 也是逗号分隔的字符串
        return Arrays.stream(raw.split(","))
            .map(String::trim)
            .collect(Collectors.toList());
    }


    // =========================================================
    // 内部逻辑 Helper
    // =========================================================

    @JsonIgnore
    private String resolveUri() {
        // 1. 优先使用显式 endpoint
        if (Objects.nonNull(this.endpoint) && !this.endpoint.isBlank()) {
            return this.endpoint;
        }

        // 2. 自动组装
        final StringBuilder uri = new StringBuilder("redis://");
        if (Objects.nonNull(this.password) && !this.password.isBlank()) {
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