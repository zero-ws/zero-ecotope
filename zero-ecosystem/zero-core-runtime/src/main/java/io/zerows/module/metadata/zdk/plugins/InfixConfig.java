package io.zerows.module.metadata.zdk.plugins;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.runtime.exception._10005Exception500PluginDynamic;
import io.zerows.module.metadata.store.OZeroStore;

import java.io.Serializable;

/**
 * Third part configuration data.
 * endpoint: Major endpoint
 * config: Configuration Data of third part.
 */
public class InfixConfig implements Serializable {

    private static final Cc<String, InfixConfig> CC_CACHE = Cc.open();

    private static final String KEY_ENDPOINT = "endpoint";
    private static final String KEY_CONFIG = "config";

    private final transient JsonObject config;
    private final transient String endpoint;

    public InfixConfig(final String key, final String rule) {
        final JsonObject raw = OZeroStore.option(key);
        Fn.jvmKo(!OZeroStore.is(key), _10005Exception500PluginDynamic.class, key, raw.encode());

        // Check up exception for JsonObject
        this.endpoint = raw.getString(KEY_ENDPOINT);
        this.config = raw.getJsonObject(KEY_CONFIG);
    }

    public static InfixConfig create(final String key) {
        return CC_CACHE.pick(() -> new InfixConfig(key, null), key);
        // return FnZero.po?l(CACHE, key, () -> new InfixConfig(key, null));
    }

    public static InfixConfig create(final String key, final String rule) {
        return CC_CACHE.pick(() -> new InfixConfig(key, rule), key);
        // return FnZero.po?l(CACHE, key, () -> new InfixConfig(key, rule));
    }

    public JsonObject getConfig() {
        return this.config;
    }

    public String getEndPoint() {
        return this.endpoint;
    }
}
