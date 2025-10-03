package io.zerows.sdk.plugins;

import io.r2mo.function.Fn;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.exception._40020Exception500PluginStatic;
import io.zerows.management.OZeroStore;

import java.util.function.Function;

public interface Infix {
    static <R> R init(final String key,
                      final Function<JsonObject, R> executor,
                      final Class<?> clazz) {
        Fn.jvmKo(!OZeroStore.is(key), _40020Exception500PluginStatic.class, key);
        final JsonObject options = OZeroStore.option(key);
        return executor.apply(options);
    }

    <T> T get();
}
