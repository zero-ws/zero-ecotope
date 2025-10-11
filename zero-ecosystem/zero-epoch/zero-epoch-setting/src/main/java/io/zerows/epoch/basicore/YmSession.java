package io.zerows.epoch.basicore;

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

/**
 * @author lang : 2025-10-09
 */
@Data
public class YmSession implements Serializable {
    @JsonProperty("store-type")
    private String storeType;

    @JsonProperty("store-component")
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> storeComponent;

    private int timeout = -1;   // 分钟

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject options;
    
    private Cookie cookie;

    @Data
    public static class Cookie implements Serializable {
        private String name;
        @JsonProperty("max-age")
        private int maxAge;
    }
}
