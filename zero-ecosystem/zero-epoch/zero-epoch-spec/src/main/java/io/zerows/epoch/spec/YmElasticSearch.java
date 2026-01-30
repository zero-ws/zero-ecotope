package io.zerows.epoch.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-11
 */
@Data
public class YmElasticSearch implements Serializable {
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray uris = new JsonArray();

    @JsonProperty("connection-timeout")
    private int connectionTimeout;

    @JsonProperty("socket-timeout")
    private int socketTimeout;

    private String username;
    private String password;
}
