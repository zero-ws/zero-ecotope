package io.zerows.unity;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.core.web.model.commune.Envelop;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-21
 */
class _Plugin extends _Offline {

    public static <T> T mountPlugin(final String key,
                                    final BiFunction<Class<?>, JsonObject, T> function,
                                    final Supplier<T> supplier) {
        return Plugin.mountPlugin(key, function, supplier);
    }

    public static void mountPlugin(final String key,
                                   final BiConsumer<Class<?>, JsonObject> function) {
        Plugin.mountPlugin(key, (clazz, config) -> {
            function.accept(clazz, config);
            return null;
        }, () -> null);
    }

    public static Future<Envelop> mountPlugin(
        final String key,
        /* No plugin, returned original Future<Envelop> */
        final Envelop envelop,
        /* Internal function for generation of Envelop */
        final BiFunction<Class<?>, JsonObject, Future<Envelop>> function) {
        return Plugin.mountPlugin(key, envelop, function);
    }
}
