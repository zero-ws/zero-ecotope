package io.zerows.epoch.corpus.model;

import io.vertx.core.json.JsonObject;

import java.io.Serializable;

public class Rule implements Serializable {

    private final String type;

    private final String message;

    private final JsonObject config = new JsonObject();

    private Rule(final JsonObject data) {
        this.type = data.getString("type");
        this.message = data.getString("message");
        this.config.mergeIn(data.copy());
        this.config.remove("type");
        this.config.remove("message");
    }

    public static Rule create(final JsonObject data) {
        return new Rule(data);
    }

    public String getMessage() {
        return this.message;
    }

    public String getType() {
        return this.type;
    }

    public JsonObject getConfig() {
        return this.config;
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
