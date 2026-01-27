package io.zerows.epoch.spec;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true, fluent = true)
public class InPreArgs implements Serializable {
    private InPreVertx.Config configVertx;
    private JsonObject options = new JsonObject();
}
