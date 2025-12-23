package io.zerows.extension.module.ui.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.MDConfig;
import io.zerows.epoch.constant.KName;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.support.Ut;
import lombok.Data;

import java.util.Objects;

/*
 * Ui Configuration data
 */
@Data
public class UiConfig implements MDConfig {

    private transient String definition;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private transient JsonObject mapping;
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private transient JsonObject attributes;

    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private transient JsonArray op;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private transient JsonObject cache;

    // Sec Expired seconds
    private transient int cacheExpired = 7200;

    public boolean okCache() {
        return Ut.isNotNil(this.cache);
    }

    public String keyControl() {
        Objects.requireNonNull(this.cache);
        return this.cache.getString(KName.Ui.CONTROLS);
    }

    public String keyOps() {
        Objects.requireNonNull(this.cache);
        return this.cache.getString("ops");
    }

    public int getCacheExpired() {
        Objects.requireNonNull(this.cache);
        return this.cacheExpired;
    }

    @Override
    public String toString() {
        return "UiConfig{" +
            "definition='" + this.definition + '\'' +
            ", mapping=" + this.mapping +
            ", attributes=" + this.attributes +
            ", op=" + this.op +
            ", cache=" + this.cache +
            ", cacheExpired=" + this.cacheExpired +
            '}';
    }
}
