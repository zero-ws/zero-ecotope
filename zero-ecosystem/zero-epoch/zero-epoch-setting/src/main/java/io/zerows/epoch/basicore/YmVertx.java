package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.application.VertxYml;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.annotations.ClassYml;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * {@link VertxYml.vertx}
 *
 * @author lang : 2025-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YmVertx extends InPreVertx implements Serializable {
    private YmVertxConfig config = new YmVertxConfig();
    private YmMvc mvc = new YmMvc();
    private ClusterOptions cluster;
    private YmDataSource datasource;
    private YmSecurity security = new YmSecurity();
    private YmVertxData data = new YmVertxData();
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
    @Data
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

        private YmVertxConfig.Delivery delivery;

        private YmVertxConfig.Deployment deployment;

        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject shared;

        @Data
        public static class Counter implements Serializable {
            private int worker;
            private int agent;
        }
    }
}
