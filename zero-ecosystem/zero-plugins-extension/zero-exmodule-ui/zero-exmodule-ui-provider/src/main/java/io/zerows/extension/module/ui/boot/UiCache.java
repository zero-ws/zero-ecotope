package io.zerows.extension.module.ui.boot;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ui.common.UiConstant;
import io.zerows.plugins.cache.HMM;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class UiCache {
    private static final MDUIManager MANAGER = MDUIManager.of();

    public static Future<JsonObject> cacheControl(final JsonObject body,
                                                  final Supplier<Future<JsonObject>> executor) {
        return getCache(MANAGER::keyControl, body, executor);
    }

    public static Future<JsonArray> cacheOps(final JsonObject body,
                                             final Supplier<Future<JsonArray>> executor) {
        return getCache(MANAGER::keyOps, body, executor);
    }

    private static <T> Future<T> getCache(
        final Supplier<String> poolFn,
        final JsonObject body,
        final Supplier<Future<T>> executor) {
        final String keyPool = poolFn.get();
        if (Ut.isNotNil(keyPool)) {
            final String uiKey = String.valueOf(body.hashCode());
            return HMM.<String, T>of(keyPool).cached(uiKey, executor);
        } else {
            log.warn("{} Ui 缓存已被禁用！", UiConstant.K_PREFIX_UI);
            return executor.get();
        }
    }
}
