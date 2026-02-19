package io.zerows.epoch.metadata;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KWeb;
import io.zerows.specification.atomic.HJson;
import io.zerows.support.Ut;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class XHeader implements Serializable, HJson {

    private String sigma;
    private String appId;
    private String appKey;
    private String language;
    // New for Cloud
    private String tenantId;
    private String session;

    private Map<String, Object> extension = new HashMap<>();

    public String session() {
        return this.session;
    }

    @Override
    public void fromJson(final JsonObject json) {
        // 扩展属性也会反序列化
        final XHeader header = Ut.deserialize(json, XHeader.class);
        if (Objects.nonNull(header)) {
            this.appId = header.appId;
            this.appKey = header.appKey;
            this.sigma = header.sigma;
            this.language = header.language;
            this.session = header.session;
            this.tenantId = header.tenantId;
            if (Objects.nonNull(header.extension)) {
                this.extension.putAll(header.extension);
            }
        }
    }

    public void fromHeader(final MultiMap headers) {
        if (Objects.nonNull(headers)) {
            this.appId = headers.get(KWeb.HEADER.X_APP_ID);
            this.appKey = headers.get(KWeb.HEADER.X_APP_KEY);
            this.sigma = headers.get(KWeb.HEADER.X_SIGMA);
            this.language = headers.get(KWeb.HEADER.X_LANG);
            this.session = headers.get(KWeb.HEADER.X_SESSION_ID);
            this.tenantId = headers.get(KWeb.HEADER.X_TENANT_ID);
            headers.names().stream()
                .filter(name -> name.startsWith("X-"))
                .forEach(name -> this.extension.put(name, headers.get(name)));
        }
    }

    // 支持反序列化的扩展属性方法
    @JsonAnyGetter
    public Map<String, Object> getExtension() {
        return this.extension;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(final String key) {
        return (T) this.extension.getOrDefault(key, null);
    }

    @JsonAnySetter
    public void setExtension(final String key, final Object value) {
        this.extension.put(key, value);
    }

    @Override
    public JsonObject toJson() {
        return Ut.serializeJson(this);
    }
}
