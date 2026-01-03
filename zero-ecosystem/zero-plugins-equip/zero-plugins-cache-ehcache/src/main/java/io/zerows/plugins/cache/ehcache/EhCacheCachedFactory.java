package io.zerows.plugins.cache.ehcache;

import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.cache.CachedFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class EhCacheCachedFactory implements CachedFactory {
    @Override
    public <K, V> MemoAt<K, V> findMemoAt(final Vertx vertx, final MemoOptions<K, V> options) {
        final JsonObject extension = options.extension();
        if (Objects.isNull(extension)) {
            return null;
        }
        return null;
    }
}
