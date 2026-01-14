package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.shareddata.Shareable;
import lombok.Data;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Redis 配置 POJO (优化版 - 解决连接池假死问题)
 * <p>
 * 适配逻辑：RedisOptions(JsonObject)
 * 核心策略：强制将密码拼接到 connectionString 中，并优化连接池与超时策略。
 * </p>
 *
 * @author lang : 2025-10-06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YmRedis implements Serializable, Shareable {

    // =========================================================
    // 1. 输入字段 (配置文件读取)
    // =========================================================
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String host = "127.0.0.1";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer port = 6379;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer database = 0;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String endpoint;

    // =========================================================
    // 2. 直通字段 (输出到 JsonObject)
    // =========================================================

    private String password;
    private String type = "STANDALONE";
    private String role = "MASTER";
    private String masterName;

    // 【重要】保持 32 不变，并发能力的基础
    private Integer maxPoolSize = 32;

    // 【关键优化】从 2048 降为 128。
    // 如果 32 个连接都在忙，且有 128 个在排队，第 129 个请求应该直接报错，
    // 而不是进入无尽的等待导致系统假死。
    private Integer maxWaitingHandlers = 128;

    private Long poolRecycleTimeout = 15000L;

    // Redis 客户端层的重连尝试（逻辑层）
    private Integer maxReconnectAttempts = 5;
    private Long reconnectInterval = 1000L;

    @JsonProperty("netClientOptions")
    private YmNet config = new YmNet();

    // =========================================================
    // 3. 计算字段 (专门给 RedisOptions 喂饭)
    // =========================================================

    @JsonProperty("connectionString")
    public String getComputedConnectionString() {
        if ("CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        return this.resolveUri();
    }

    @JsonProperty("endpoints")
    public List<String> getComputedEndpoints() {
        if (!"CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        if (Objects.nonNull(this.endpoint) && !this.endpoint.isBlank()) {
            return Arrays.stream(this.endpoint.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        }
        return Collections.singletonList(this.resolveUri());
    }

    // =========================================================
    // 4. URI 组装逻辑 (强制带密码)
    // =========================================================
    @JsonIgnore
    private String resolveUri() {
        // 1. 优先使用 connectionString 全路径覆盖
        if (Objects.nonNull(this.endpoint) && !this.endpoint.isBlank() && !this.endpoint.contains(",")) {
            return this.endpoint.startsWith("redis://") ? this.endpoint : "redis://" + this.endpoint;
        }

        // 2. 自动组装
        final StringBuilder uri = new StringBuilder("redis://");

        // 密码拼接 (URL Encode 防止特殊字符破坏格式)
        if (Objects.nonNull(this.password) && !this.password.isBlank()) {
            final String encodedPass = URLEncoder.encode(this.password, StandardCharsets.UTF_8);
            uri.append(":").append(encodedPass).append("@");
        }

        uri.append(this.host).append(":").append(this.port);

        // 单机模式拼 DB 号
        if (Objects.nonNull(this.database) && !"CLUSTER".equalsIgnoreCase(this.type)) {
            uri.append("/").append(this.database);
        }

        return uri.toString();
    }

    // =========================================================
    // 5. NetClient 配置 (TCP层优化)
    // =========================================================
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class YmNet implements Serializable {

        // 【优化】连接超时从 10000ms 降为 3000ms，快速感知网络故障
        private Integer connectTimeout = 3000;

        // 【优化】设置为 30 (秒)。
        // 这里的单位通常是秒(Vert.x NetClient标准)。
        // 强制回收空闲超过 30秒 的连接，防止防火墙切断后产生的死链接。
        private Integer idleTimeout = 30;

        // 保持 KeepAlive 开启，检测死链
        private Boolean tcpKeepAlive = true;

        // 禁用 Nagle 算法，减少小包延迟
        private Boolean tcpNoDelay = true;

        private Boolean soKeepAlive = true;
        private Boolean ssl = false;
        private Boolean trustAll = true;
        private String hostnameVerificationAlgorithm = "";
    }
}