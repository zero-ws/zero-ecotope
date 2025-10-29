package io.zerows.epoch.basicore;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonObject;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-05
 */
@Data
public class YmSecurity implements Serializable {
    private Jwt jwt;
    private User user;
    private ConcurrentMap<String, JsonObject> config = new ConcurrentHashMap<>();

    /**
     * @author lang : 2025-10-05
     */
    @Data
    public static class Jwt implements Serializable {
        @JsonSerialize(using = JsonObjectSerializer.class)
        @JsonDeserialize(using = JsonObjectDeserializer.class)
        private JsonObject options = new JsonObject();
    }

    /**
     * @author lang : 2025-10-28
     */
    @Data
    public static class User implements Serializable {
        private String name;
        private String password;
        private String roles;

        public List<String> roles() {
            if (Objects.isNull(this.roles)) {
                return Collections.emptyList();
            }
            return Arrays.asList(this.roles.split(","));
        }
    }
}
