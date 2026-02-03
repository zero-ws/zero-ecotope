package io.zerows.cortex.metadata;

import io.vertx.core.json.JsonObject;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class WebRule implements Serializable {

    private final String type;

    private final String message;

    private final JsonObject config = new JsonObject();

    private WebRule(final JsonObject data) {
        this.type = data.getString("type");
        this.message = data.getString("message");
        this.config.mergeIn(data.copy());
        this.config.remove("type");
        this.config.remove("message");
    }

    public static WebRule create(final JsonObject data) {
        return new WebRule(data);
    }

    @Override
    public String toString() {
        return "Rule{" +
            "type='" + this.type + '\'' +
            ", message='" + this.message + '\'' +
            ", config=" + this.config.encode() +
            '}';
    }
}
