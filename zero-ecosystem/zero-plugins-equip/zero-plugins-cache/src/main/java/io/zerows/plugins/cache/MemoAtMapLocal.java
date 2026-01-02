package io.zerows.plugins.cache;

import io.r2mo.vertx.common.cache.MemoAt;
import io.r2mo.vertx.common.cache.MemoOptions;
import io.vertx.core.Future;
import io.vertx.core.shareddata.AsyncMap;

/**
 * 本地内存异步 Map (LocalAsyncMap)。
 * 需要防御性拷贝以防止引用污染。
 *
 * @author lang : 2026-01-02
 */
public class MemoAtMapLocal<K, V> extends MemoAtMap<K, V> {

    private MemoAtMapLocal(final String name, final MemoOptions<K, V> options) {
        // Local 模式：建议传入深拷贝逻辑，这里暂用 v->v (实际建议从 options 获取 copier)
        // 示例：super(name, options, JsonObject::copy);
        super(name, options, v -> v);
    }

    public static <K, V> MemoAt<K, V> of(final String name, final MemoOptions<K, V> options) {
        return of(name, options, MemoAtMapLocal.class);
    }

    @Override
    protected Future<AsyncMap<K, V>> supplyMap() {
        return this.vertx().sharedData().getLocalAsyncMap(this.name());
    }
}