package io.zerows.plugins.cache;

import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Vertx;

/**
 * 针对缓存的工厂设定，底层 SPI 必备，只实现一个 SPI 即可
 */
public interface CachedFactory {
    /**
     * 追加 cache 对应配置创建缓存，cache 节点中的配置为第一优先级
     *
     * @param vertx   Vertx 实例
     * @param options 缓存配置
     * @param <K>     键类型
     * @param <V>     值类型
     * @return 缓存实例
     */
    <K, V> MemoAt<K, V> findConfigured(Vertx vertx, MemoOptions<K, V> options);

    /**
     * 查找基础缓存实例，直接创建，不加载 vertx.yml 中的配置
     *
     * @param vertx   Vertx 实例
     * @param options 缓存配置
     * @param <K>     键类型
     * @param <V>     值类型
     * @return 缓存实例
     */
    <K, V> MemoAt<K, V> findBy(Vertx vertx, MemoOptions<K, V> options);
}
