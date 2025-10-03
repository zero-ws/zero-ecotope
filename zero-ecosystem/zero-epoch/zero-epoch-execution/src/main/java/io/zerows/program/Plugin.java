package io.zerows.program;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.OLog;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.management.OZeroStore;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-21
 */

/*
 * Default package scope tool for extension.
 */
class Plugin {
    private static final JsonObject PLUGIN_CONFIG = new JsonObject();
    private static final OLog LOGGER = Ut.Log.ux(Plugin.class);

    /*
     * I/O read for config loading.
     */
    static {
        if (OZeroStore.is(YmlCore.extension.__KEY)) {
            final JsonObject pluginConfig = OZeroStore.option(YmlCore.extension.__KEY);
            if (Objects.nonNull(pluginConfig)) {
                PLUGIN_CONFIG.mergeIn(pluginConfig);
            }
        }
    }

    static <T> T mountPlugin(final String key, final BiFunction<Class<?>, JsonObject, T> function,
                             final Supplier<T> supplier) {
        if (PLUGIN_CONFIG.containsKey(key)) {
            final JsonObject metadata = PLUGIN_CONFIG.getJsonObject(key);
            final Class<?> pluginCls = Ut.clazz(metadata.getString(YmlCore.extension.COMPONENT));
            if (Objects.nonNull(pluginCls)) {
                final JsonObject config = metadata.getJsonObject(YmlCore.extension.CONFIG);
                try {
                    return function.apply(pluginCls, config);
                } catch (final Throwable ex) {
                    ex.printStackTrace();
                    LOGGER.warn("Infusion Extension Failure: {0}, class = {1}", ex.getMessage(), pluginCls);
                    return supplier.get();
                }
            }
        }
        return supplier.get();
    }

    static Future<Envelop> mountPlugin(
        final String key,
        /* No plugin, returned original Future<Envelop> */
        final Envelop envelop,
        /* Internal function for generation of Envelop */
        final BiFunction<Class<?>, JsonObject, Future<Envelop>> function) {
        return mountPlugin(key, function, () -> Ut.future(envelop));
    }
}
