package io.zerows.plugins.cache;

import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;

/**
 * 分布式集群 Map (ClusterWideMap)。
 * 依赖序列化机制天然隔离引用，无需额外的防御性拷贝。
 *
 * @author lang : 2026-01-02
 */
public class MemoAtMapCluster<K, V> extends MemoAtMap<K, V> {

    // 必须保留此构造函数供反射调用 (SourceReflect.instance)
    public MemoAtMapCluster(final Vertx vertx, final MemoOptions<K, V> options) {
        // Cluster 模式：直接透传 (v -> v)，因为底层序列化已经做了 Deep Copy
        super(vertx, options, v -> v);
    }

    @Override
    protected Future<AsyncMap<K, V>> supplyMap() {
        return this.vertx().sharedData().getClusterWideMap(this.name());
    }
}