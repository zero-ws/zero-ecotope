package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-06
 */
@Data
public class YmDubbo implements Serializable {

    private static final String KEY_SERIALIZE_CHECK_STATUS = "serialize-check-status";
    private static final String KEY_SERIALIZATION_SECURITY_CHECK = "serialization-security-check";

    private Application application;

    private Registry registry;

    private Protocol protocol;

    private ServiceConfig provider;

    private ServiceConfig consumer;

    @Data
    public static class Application implements Serializable {
        private String name;
        private int qosPort;
        @JsonProperty(KEY_SERIALIZE_CHECK_STATUS)
        private String serializeCheckStatus;
    }

    @Data
    public static class Registry implements Serializable {
        private String address;
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject parameters;
    }

    @Data
    public static class Protocol implements Serializable {
        private String name;
        private int port;
    }

    @Data
    public static class ServiceConfig implements Serializable {
        @JsonProperty(KEY_SERIALIZATION_SECURITY_CHECK)
        private boolean serializationSecurityCheck;
    }
}
