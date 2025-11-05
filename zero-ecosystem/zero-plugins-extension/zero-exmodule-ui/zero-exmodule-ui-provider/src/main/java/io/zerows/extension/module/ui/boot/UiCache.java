package io.zerows.extension.module.ui.boot;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.component.log.LogOf;
import io.zerows.cosmic.plugins.cache.Rapid;
import io.zerows.support.Ut;

import java.util.function.Supplier;

import static io.zerows.extension.module.ui.boot.Ui.LOG;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class UiCache {

    private static final LogOf LOGGER = LogOf.get(UiCache.class);

    public static Future<JsonObject> cacheControl(final JsonObject body,
                                                  final Supplier<Future<JsonObject>> executor) {
        return getCache(UiPin::keyControl, body, executor);
    }

    public static Future<JsonArray> cacheOps(final JsonObject body,
                                             final Supplier<Future<JsonArray>> executor) {
        return getCache(UiPin::keyOps, body, executor);
    }

    private static <T> Future<T> getCache(
        final Supplier<String> poolFn,
        final JsonObject body,
        final Supplier<Future<T>> executor) {
        final String keyPool = poolFn.get();
        if (Ut.isNotNil(keyPool)) {
            final String uiKey = String.valueOf(body.hashCode());
            return Rapid.<String, T>object(keyPool).cached(uiKey, executor);
        } else {
            LOG.Ui.info(LOGGER, "Ui Cached has been disabled!");
            return executor.get();
        }
    }
}
