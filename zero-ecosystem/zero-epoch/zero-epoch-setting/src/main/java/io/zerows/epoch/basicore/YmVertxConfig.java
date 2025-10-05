package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.zerows.epoch.basicore.option.CorsOptions;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.platform.annotations.ClassYml;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lang : 2025-10-05
 */
@Data
public class YmVertxConfig implements Serializable {
    private static final String KEY_IMPORT = "import";

    @JsonProperty(KEY_IMPORT)
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray imports;

    private List<Instance> instance;

    private Delivery delivery;

    private Deployment deployment;

    /**
     * @author lang : 2025-10-05
     */
    @Data
    public static class Delivery implements Serializable {
        private long timeout = 30000L;
        private String codecName;
        private MultiMap headers;
        private boolean localOnly = false;
        private TracingPolicy tracingPolicy;
    }

    /**
     * @author lang : 2025-10-05
     */
    @Data
    public static class Deployment implements Serializable {

        private Instance.Counter instances;

        /*
         * 特殊情况相关配置，如果存在则
         * - componentName = JsonObject
         **/
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options;
    }

    /**
     * @author lang : 2025-10-05
     */
    @ClassYml
    public static class Instance implements Serializable {
        private String name;

        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options;

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
        private CorsOptions cors;
    }
}
