package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.annotations.ClassYml;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link VertxYml.vertx}
 *
 * @author lang : 2025-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YmVertx extends InPreVertx implements Serializable {
    private Config config = new Config();
    private YmMvc mvc = new YmMvc();
    private ClusterOptions cluster;
    private YmDataSource datasource;
    private YmSecurity security = new YmSecurity();
    private Data data = new Data();
    private YmSession session;
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject shared;

    /**
     * 🏗️ 实例配置类
     * <pre>
     *     📋 Options 属性默认值表：
     *     ┌───────────────────────────┬───────────────────────┬─────────────────────────┐
     *          🏷️ 配置项                    📝 默认值                 🎯 说明
     *     ├───────────────────────────┼───────────────────────┼─────────────────────────┤
     *        maxEventLoopExecuteTime      1200_000_000_000L      事件循环最大执行时间(纳秒)
     *        maxWorkerExecuteTime         1200_000_000_000L      工作线程最大执行时间(纳秒)
     *        eventLoopPoolSize            128                    事件循环池大小
     *        workerPoolSize               256                    工作线程池大小
     *        internalBlockingPoolSize     128                    内部阻塞线程池大小
     *        preferNativeTransport        true                   优先使用原生传输
     *     └───────────────────────────┴───────────────────────┴─────────────────────────┘
     * </pre>
     * <pre>
     *     🎯 功能说明：
     *     - 配置 Vert.x 实例的核心参数
     *     - 管理事件循环和工作线程池大小
     *     - 控制执行时间和传输方式
     *     - 提供实例计数器配置
     * </pre>
     *
     * @author lang : 2025-10-05
     */
    @ClassYml
    @lombok.Data
    public static class Instance implements Serializable {
        private String name;

        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options = new JsonObject()
            .put("maxEventLoopExecuteTime", 1200_000_000_000L)
            .put("maxWorkerExecuteTime", 1200_000_000_000L)
            .put("eventLoopPoolSize", 128)
            .put("workerPoolSize", 256)
            .put("internalBlockingPoolSize", 128)
            .put("preferNativeTransport", true);

        private Delivery delivery;

        private Deployment deployment;

        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject shared;

        @lombok.Data
        public static class Counter implements Serializable {
            private int worker;
            private int agent;
        }
    }

    /**
     * 📦 消息投递配置类
     * <pre>
     *     📋 属性默认值表：
     *     ┌─────────────────────┬──────────────────────────┬──────────────────────────┐
     *        🏷️ 属性名称               📝 默认值                      🎯 说明
     *     ├─────────────────────┼──────────────────────────┼──────────────────────────┤
     *        ⏰ timeout              30000L                       超时时间(毫秒)
     *        🔧 codecName            null                         编解码器名称
     *        📨 headers              new JsonObject()             消息头信息
     *        🏠 localOnly            false                        本地投递限制
     *        📍 tracingPolicy        TracingPolicy.IGNORE         追踪策略
     *     └─────────────────────┴──────────────────────────┴──────────────────────────┘
     * </pre>
     * <pre>
     *     🎯 功能说明：
     *     - 配置消息投递的超时时间（默认 30000L 毫秒）
     *     - 管理消息头信息（默认 new JsonObject()）
     *     - 控制投递范围（默认 false，表示可跨节点投递）
     *     - 指定编解码器名称（默认 null）
     *     - 设置追踪策略（默认 TracingPolicy.IGNORE）
     * </pre>
     *
     * @author lang : 2025-10-05
     */
    @lombok.Data
    public static class Delivery implements Serializable {
        private long timeout = 30000L;
        private String codecName;
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject headers = new JsonObject();
        private boolean localOnly = false;
        private TracingPolicy tracingPolicy = TracingPolicy.IGNORE;
    }

    /**
     * @author lang : 2025-10-05
     */
    @lombok.Data
    public static class Deployment implements Serializable {

        private Instance.Counter instances = new Instance.Counter();

        /*
         * 特殊情况相关配置，如果存在则
         * - componentName = JsonObject
         **/
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options = new JsonObject();
    }

    /**
     * @author lang : 2025-10-05
     */
    @lombok.Data
    @EqualsAndHashCode(callSuper = true)
    public static class Config extends InPreVertx.Config implements Serializable {

        private List<Instance> instance = new ArrayList<>();

        private Delivery delivery = new Delivery();

        private Deployment deployment = new Deployment();

        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject shared;

        public List<Instance> getInstance() {
            if (this.instance.isEmpty()) {
                final Instance instance = new Instance();
                instance.setName(Ut.randomString(16));
                this.instance.add(instance);
            }
            return this.instance;
        }

    }

    /**
     * @author lang : 2025-10-06
     */
    @lombok.Data
    public static class Data implements Serializable {
        private YmRedis redis;
    }
}
