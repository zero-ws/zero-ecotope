package io.zerows.module.metadata.zdk.plugins;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.module.metadata.exception.BootDynamicKeyMissingException;
import io.zerows.module.metadata.store.OZeroStore;
import io.zerows.module.metadata.uca.logging.OLog;

import java.io.Serializable;

/**
 * Third part configuration data.
 * endpoint: Major endpoint
 * config: Configuration Data of third part.
 */
public class InfixConfig implements Serializable {

    private static final OLog LOGGER = Ut.Log.configure(InfixConfig.class);

    private static final Cc<String, InfixConfig> CC_CACHE = Cc.open();

    private static final String KEY_ENDPOINT = "endpoint";
    private static final String KEY_CONFIG = "config";

    private final transient JsonObject config;
    private final transient String endpoint;

    public InfixConfig(final String key, final String rule) {
        final JsonObject raw = OZeroStore.option(key);
        // Check up exception for key
        Fx.outBoot(!OZeroStore.is(key),
            LOGGER, BootDynamicKeyMissingException.class,
            this.getClass(), key, raw);

        // Check up exception for JsonObject
        this.endpoint = raw.getString(KEY_ENDPOINT);
        this.config = raw.getJsonObject(KEY_CONFIG);
    }

    public static InfixConfig create(final String key) {
        return CC_CACHE.pick(() -> new InfixConfig(key, null), key);
        // return Fx.po?l(CACHE, key, () -> new InfixConfig(key, null));
    }

    public static InfixConfig create(final String key, final String rule) {
        return CC_CACHE.pick(() -> new InfixConfig(key, rule), key);
        // return Fx.po?l(CACHE, key, () -> new InfixConfig(key, rule));
    }

    public JsonObject getConfig() {
        return this.config;
    }

    public String getEndPoint() {
        return this.endpoint;
    }
}
