package io.zerows.plugins.cache;

import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;

/**
 * 针对缓存的工厂设定，底层 SPI 必备，只实现一个 SPI 即可
 */
public interface CachedFactory {

    <K, V> MemoAt<K, V> createMemoAt(Vertx vertx, MemoOptions<K, V> options);
}
