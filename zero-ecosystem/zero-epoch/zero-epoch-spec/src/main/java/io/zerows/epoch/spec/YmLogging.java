package io.zerows.epoch.spec;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author lang : 2025-10-06
 */
@Data
public class YmLogging implements Serializable {
    private Map<String, String> level = Map.of();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject file = new JsonObject();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject logback = new JsonObject();
}
