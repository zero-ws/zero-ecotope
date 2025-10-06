package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.annotations.ClassYml;
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
public class YmVertxConfig extends YmEntrance.Config implements Serializable {

    private List<Instance> instance = new ArrayList<>();

    private Delivery delivery = new Delivery();

    private Deployment deployment = new Deployment();

    /**
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
        private TracingPolicy tracingPolicy;
    }

    /**
     * @author lang : 2025-10-05
     */
    @Data
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
    @ClassYml
    public static class Instance implements Serializable {
        private String name;

        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options = new JsonObject();

        @Data
        public static class Counter implements Serializable {
            private int worker;
            private int agent;
        }
    }

    /**
     * @author lang : 2025-10-05
     */
    @Data
    public static class Application implements Serializable {
        private String name;
        private CorsOptions cors = new CorsOptions();
    }
}
