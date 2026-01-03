package io.zerows.plugins.cache;

import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.AsyncMap;

/**
 * 本地内存异步 Map (LocalAsyncMap)。
 * 需要防御性拷贝以防止引用污染。
 *
 * @author lang : 2026-01-02
 */
public class MemoAtMapLocal<K, V> extends MemoAtMap<K, V> {

    // 必须保留此构造函数供反射调用 (SourceReflect.instance)
    public MemoAtMapLocal(final Vertx vertx, final MemoOptions<K, V> options) {
        // Local 模式：建议传入深拷贝逻辑，这里暂用 v->v (实际建议从 options 获取 copier)
        super(vertx, options, v -> v);
    }

    @Override
    protected Future<AsyncMap<K, V>> supplyMap() {
        return this.vertx().sharedData().getLocalAsyncMap(this.name());
    }
}