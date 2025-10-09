package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YmVertxConfig extends InPreVertx.Config implements Serializable {

    private List<YmVertx.Instance> instance = new ArrayList<>();

    private Delivery delivery = new Delivery();

    private Deployment deployment = new Deployment();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject shared;

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
    @Data
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
    @Data
    public static class Deployment implements Serializable {

        private YmVertx.Instance.Counter instances = new YmVertx.Instance.Counter();

        /*
         * 特殊情况相关配置，如果存在则
         * - componentName = JsonObject
         **/
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options = new JsonObject();
    }

}
