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
 * Redis 配置 POJO (最终修正版)
 * <p>
 * 适配逻辑：RedisOptions(JsonObject)
 * 核心策略：强制将密码拼接到 connectionString 中，确保 Vert.x 客户端初始化即带认证信息。
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

    // 显式配置的 endpoint (如 redis://...)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String endpoint;

    // =========================================================
    // 2. 直通字段 (输出到 JsonObject)
    // =========================================================

    // 保留 password 字段，以此作为双重保险。
    // 即使 connectionString 解析失败，Vert.x 也有机会读到这个字段。
    private String password;

    private String type = "STANDALONE";
    private String role = "MASTER";
    private String masterName;

    private Integer maxPoolSize = 32;
    private Integer maxWaitingHandlers = 2048;
    private Long poolRecycleTimeout = 15000L;
    private Integer maxReconnectAttempts = 5;
    private Long reconnectInterval = 1000L;

    @JsonProperty("netClientOptions")
    private YmNet config = new YmNet();

    // =========================================================
    // 3. 计算字段 (专门给 RedisOptions 喂饭)
    // =========================================================

    /**
     * 生成 connectionString
     * 结果示例： "redis://:lang1017@127.0.0.1:6379/0"
     */
    @JsonProperty("connectionString")
    public String getComputedConnectionString() {
        if ("CLUSTER".equalsIgnoreCase(this.type)) {
            return null; // 集群模式不看 connectionString
        }
        return this.resolveUri();
    }

    /**
     * 生成 endpoints (集群模式专用)
     */
    @JsonProperty("endpoints")
    public List<String> getComputedEndpoints() {
        if (!"CLUSTER".equalsIgnoreCase(this.type)) {
            return null;
        }
        // 集群模式下，如果手动配置了 endpoint，解析它
        if (Objects.nonNull(this.endpoint) && !this.endpoint.isBlank()) {
            return Arrays.stream(this.endpoint.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        }
        // 否则用当前配置生成一个带密码的单点作为入口
        return Collections.singletonList(this.resolveUri());
    }

    // =========================================================
    // 4. URI 组装逻辑 (强制带密码)
    // =========================================================
    @JsonIgnore
    private String resolveUri() {
        // 1. 优先使用 connectionString 全路径覆盖
        if (Objects.nonNull(this.endpoint) && !this.endpoint.isBlank() && !this.endpoint.contains(",")) {
            // 如果用户自己在 yaml 里写了 endpoint，假设他已经拼好了密码
            // 但为了保险，建议还是走下面的自动组装
            return this.endpoint.startsWith("redis://") ? this.endpoint : "redis://" + this.endpoint;
        }

        // 2. 自动组装
        final StringBuilder uri = new StringBuilder("redis://");

        // 🔥 核心修正：密码拼接
        if (Objects.nonNull(this.password) && !this.password.isBlank()) {
            // URL Encode 主要是防止密码里有 @ / : 等特殊字符破坏 URI 结构
            final String encodedPass = URLEncoder.encode(this.password, StandardCharsets.UTF_8);
            // Redis URI 规范： redis://[user]:[password]@[host]...
            // 用户名通常为空，所以是冒号开头
            uri.append(":").append(encodedPass).append("@");
        }

        uri.append(this.host).append(":").append(this.port);

        // 单机模式才拼 DB 号
        if (Objects.nonNull(this.database) && !"CLUSTER".equalsIgnoreCase(this.type)) {
            uri.append("/").append(this.database);
        }

        return uri.toString();
    }

    // =========================================================
    // 5. NetClient 配置
    // =========================================================
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class YmNet implements Serializable {
        private Integer connectTimeout = 10000;
        private Integer idleTimeout = 0;
        private Boolean tcpKeepAlive = true;
        private Boolean tcpNoDelay = true;
        private Boolean soKeepAlive = true;
        private Boolean ssl = false;
        private Boolean trustAll = true;
        private String hostnameVerificationAlgorithm = "";
    }
}