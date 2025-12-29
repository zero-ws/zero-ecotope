package io.zerows.plugins.monitor.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 对应扩展数据结构
 * <pre>
 *     monitor:
 *       server:                        # 服务端：JMX / 监控核心配置
 *         jmx-host:
 *         jmx-port:
 *         jmx-step:
 *         monitor-type:                # 枚举值（内置专用）
 *         monitor-component: ???       # 扩展专用
 *         monitor-config: {@link JsonObject}
 *       client:
 *       - name: ???
 *         component: ???
 *         enabled: true/false
 *       roles:
 *       - id: ???
 *         component: client name 引用启用的 client
 *         config: {@link JsonObject}
 * </pre>
 *
 * @author lang : 2025-12-29
 */
@Data
public class YmMonitor implements Serializable {
    /**
     * 服务端配置：JMX / 监控核心容器配置
     */
    @JsonProperty("server")
    private Server server;

    /**
     * 客户端插件定义列表 (Plugin Definitions)
     * <p>定义系统中有哪些可用的监控能力</p>
     */
    @JsonProperty("client")
    private List<Client> clients = new ArrayList<>();

    /**
     * 监控角色/目标实例列表 (Instance Definitions)
     * <p>定义具体的监控任务，引用 client 并赋予具体配置</p>
     */
    @JsonProperty("roles")
    private List<Role> roles = new ArrayList<>();

    /* =========================================================================
     * 内部静态类定义 (Inner Static Classes)
     * =========================================================================
     */

    /**
     * 服务端配置节点
     */
    @Data
    public static class Server implements Serializable {

        @JsonProperty("jmx-host")
        private String jmxHost;

        @JsonProperty("jmx-step")
        private int jmxStep = 10;

        @JsonProperty("jmx-port")
        private Integer jmxPort;

        /**
         * 监控类型（内置专用），如：HAWTIO, PROMETHEUS, LOGGING
         */
        @JsonProperty("monitor-type")
        private MonitorType monitorType = MonitorType.HAWTIO;

        /**
         * 监控扩展组件类全路径（扩展专用）
         * <p>当 monitor-type 无法满足时，加载此 Class 实现自定义 Server</p>
         */
        @JsonProperty("monitor-component")
        @JsonSerialize(using = ClassSerializer.class)
        @JsonDeserialize(using = ClassDeserializer.class)
        private Class<?> monitorComponent;

        /**
         * 传递给 Server 实现的扁平化配置
         */
        @JsonProperty("monitor-config")
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject monitorConfig = new JsonObject();
    }

    /**
     * 客户端/插件定义节点
     */
    @Data
    public static class Client implements Serializable {

        /**
         * 插件唯一名称/别名，供 roles 引用
         */
        @JsonProperty("name")
        private String name;

        /**
         * SPI 插件实现类的全限定名
         */
        @JsonProperty("component")
        @JsonSerialize(using = ClassSerializer.class)
        @JsonDeserialize(using = ClassDeserializer.class)
        private Class<?> component;

        /**
         * 是否启用该插件能力
         */
        @JsonProperty("enabled")
        private Boolean enabled = Boolean.TRUE;
    }

    /**
     * 监控角色/实例节点
     */
    @Data
    public static class Role implements Serializable {

        /**
         * 监控实例的唯一 ID (将作为 JMX ObjectName 的 name 部分)
         */
        @JsonProperty("id")
        private String id;

        /**
         * 引用 {@link Client#name}，指定使用哪种监控能力
         */
        @JsonProperty("component")
        private String component;

        /**
         * 传递给 SPI 插件的具体运行时配置 (如 host, db, pool-size)
         */
        @JsonProperty("config")
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject config = new JsonObject();
    }
}
