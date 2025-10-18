package io.zerows.epoch.basicore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.dbe.Database;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lang : 2025-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class YmDataSource extends Database implements Serializable {

    private Dynamic dynamic = new Dynamic();

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject hikari;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject tomcat;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject dbcp2;


    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    @JsonProperty("user-cp")
    private JsonObject userCp;

    @Data
    public static class Dynamic implements Serializable {
        private String primary;
        private boolean strict;
        private Map<String, Database> datasource = new ConcurrentHashMap<>();
    }
}
