package io.zerows.plugins.cache.caffeine;

import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;
import io.zerows.plugins.cache.CachedFactory;

public class CaffeineCachedFactory implements CachedFactory {
    @Override
    public <K, V> MemoAt<K, V> findMemoAt(final Vertx vertx, final MemoOptions<K, V> options) {
        return null;
    }
}
