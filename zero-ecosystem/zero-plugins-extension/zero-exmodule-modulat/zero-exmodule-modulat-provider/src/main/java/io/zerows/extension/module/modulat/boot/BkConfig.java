package io.zerows.extension.module.modulat.boot;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.web.MDConfig;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.support.Ut;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
@Data
public class BkConfig implements MDConfig {
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray buildIn;

    public Set<String> buildIn() {
        return Ut.toSet(this.buildIn);
    }
}
