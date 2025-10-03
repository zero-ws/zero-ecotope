package io.zerows.platform.metadata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VName;
import io.zerows.integrated.jackson.JsonArrayDeserializer;
import io.zerows.integrated.jackson.JsonArraySerializer;
import io.zerows.integrated.jackson.JsonObjectDeserializer;
import io.zerows.integrated.jackson.JsonObjectSerializer;
import io.zerows.specification.atomic.HCopier;
import io.zerows.support.base.UtBase;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class KGlobal implements Serializable, HCopier<KGlobal> {
    @JsonIgnore
    private final ConcurrentMap<String, KIntegration> integrationMap = new ConcurrentHashMap<>();
    @JsonIgnore
    private final ConcurrentMap<String, String> vendorMap = new ConcurrentHashMap<>();
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject global;
    @JsonSerialize(using = JsonArraySerializer.class)
    @JsonDeserialize(using = JsonArrayDeserializer.class)
    private JsonArray source;
    private ConcurrentMap<String, JsonObject> mapping = new ConcurrentHashMap<>();
    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject application;

    @JsonSerialize(using = JsonObjectSerializer.class)
    @JsonDeserialize(using = JsonObjectDeserializer.class)
    private JsonObject integration;

    private ConcurrentMap<String, JsonObject> forbidden = new ConcurrentHashMap<>();

    private ConcurrentMap<String, JsonObject> dictionary = new ConcurrentHashMap<>();

    public ConcurrentMap<String, JsonObject> getDictionary() {
        return this.dictionary;
    }

    public void setDictionary(final ConcurrentMap<String, JsonObject> dictionary) {
        this.dictionary = dictionary;
    }

    public ConcurrentMap<String, JsonObject> getForbidden() {
        return this.forbidden;
    }

    public void setForbidden(final ConcurrentMap<String, JsonObject> forbidden) {
        this.forbidden = forbidden;
    }

    public JsonObject getIntegration() {
        return this.integration;
    }

    public void setIntegration(final JsonObject integration) {
        this.integration = integration;
        if (UtBase.isNotNil(integration)) {
            // KIntegration Configuration
            UtBase.<JsonObject>itJObject(integration, (vendor, name) -> {
                final String configFile = vendor.getString(VName.CONFIG, null);
                Objects.requireNonNull(configFile);
                // Basic Information of
                final KIntegration config = new KIntegration();
                config.fromFile(configFile);
                // Vendor Name
                final String vendorName = vendor.getString(VName.NAME);
                config.setVendorConfig(vendorName);
                config.setVendor(name);
                this.integrationMap.put(name, config);
            });
        }
    }

    public KIntegration integration(final String key) {
        return this.integrationMap.getOrDefault(key, null);
    }

    public Set<String> vendors() {
        return this.integrationMap.keySet();
    }

    public JsonObject getGlobal() {
        return Objects.isNull(this.global) ? new JsonObject() : this.global.copy();
    }

    public void setGlobal(final JsonObject global) {
        this.global = global;
    }

    public JsonArray getSource() {
        return this.source;
    }

    public void setSource(final JsonArray source) {
        this.source = source;
    }

    public ConcurrentMap<String, JsonObject> getMapping() {
        return this.mapping;
    }

    public void setMapping(final ConcurrentMap<String, JsonObject> mapping) {
        this.mapping = mapping;
    }

    public JsonObject getApplication() {
        final JsonObject application = Objects.isNull(this.application) ? new JsonObject() : this.application.copy();
        if (Objects.isNull(this.global)) {
            return application;
        } else {
            return UtBase.valueCopy(application, this.global,
                VName.APP_ID, VName.SIGMA, VName.APP_KEY
            );
        }
    }

    public void setApplication(final JsonObject application) {
        this.application = application;
    }

    public String appId() {
        return UtBase.valueString(this.global, VName.APP_ID);
    }

    public String sigma() {
        return UtBase.valueString(this.global, VName.SIGMA);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <CHILD extends KGlobal> CHILD copy() {
        final KGlobal tenant = new KGlobal();
        tenant.application = this.application.copy();
        tenant.global = this.global.copy();
        tenant.integration = this.integration.copy();
        tenant.source = this.source.copy();
        // Vendor
        tenant.vendorMap.clear();
        tenant.vendorMap.putAll(this.vendorMap);
        // Mapping
        tenant.mapping.clear();
        this.mapping.forEach((key, item) -> tenant.mapping.put(key, item.copy()));
        // KIntegration
        tenant.integrationMap.clear();
        this.integrationMap.forEach((key, integration) -> tenant.integrationMap.put(key, integration.copy()));
        return (CHILD) tenant;
    }

    @Override
    public String toString() {
        return "KGlobal{" +
            "vendorMap=" + this.vendorMap +
            ", global=" + this.global +
            ", source=" + this.source +
            ", mapping=" + this.mapping +
            ", application=" + this.application +
            ", integration=" + this.integration +
            '}';
    }
}
