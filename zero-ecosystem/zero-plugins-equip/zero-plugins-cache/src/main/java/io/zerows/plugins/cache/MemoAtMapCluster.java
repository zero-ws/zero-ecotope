package io.zerows.plugins.cache;

import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.shareddata.AsyncMap;

/**
 * 分布式集群 Map (ClusterWideMap)。
 * 依赖序列化机制天然隔离引用，无需额外的防御性拷贝。
 *
 * @author lang : 2026-01-02
 */
public class MemoAtMapCluster<K, V> extends MemoAtMap<K, V> {

    private MemoAtMapCluster(final String name, final MemoOptions<K, V> options) {
        // Cluster 模式：直接透传 (v -> v)，因为底层序列化已经做了 Deep Copy
        super(name, options, v -> v);
    }

    public static <K, V> MemoAt<K, V> of(final String name, final MemoOptions<K, V> options) {
        return of(name, options, MemoAtMapCluster.class);
    }

    @Override
    protected Future<AsyncMap<K, V>> supplyMap() {
        return this.vertx().sharedData().getClusterWideMap(this.name());
    }
}