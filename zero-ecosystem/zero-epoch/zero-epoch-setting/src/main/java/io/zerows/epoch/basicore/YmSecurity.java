package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-05
 */
@Data
public class YmSecurity implements Serializable {
    private String wall;
    private Jwt jwt;

    /**
     * @author lang : 2025-10-05
     */
    @Data
    public static class Jwt implements Serializable {
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options;
    }
}
