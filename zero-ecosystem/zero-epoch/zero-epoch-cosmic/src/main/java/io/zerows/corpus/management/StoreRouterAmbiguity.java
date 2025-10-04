package io.zerows.corpus.management;

import io.zerows.epoch.corpus.model.running.RunRoute;
import io.zerows.epoch.corpus.model.running.RunThread;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 路由存储器，用来存储当前环境中的所有路由信息
 *
 * @author lang : 2024-06-14
 */
class StoreRouterAmbiguity extends AbstractAmbiguity implements StoreRouter {
    /**
     * 路由存储，基本路由存储格式和路由本身的提取有关，基础路由信息如
     * <pre><code>
     *     1. key ( method + uri ) = RunRoute
     *     2. RunRoute 中会包含线程集合，线程集合中记录了横向线程的扩展流程，最终形成的路由结构如
     *        Thread / RunRoute = N / 1
     * </code></pre>
     */
    private final ConcurrentMap<String, RunRoute> running = new ConcurrentHashMap<>();

    protected StoreRouterAmbiguity(final HBundle owner) {
        super(owner);
    }

    @Override
    public Set<String> keys() {
        return this.running.keySet();
    }

    @Override
    public RunRoute valueGet(final String key) {
        return this.running.getOrDefault(key, null);
    }

    @Override
    public StoreRouter add(final RunRoute runRoute) {
        Objects.requireNonNull(runRoute);
        final String key = runRoute.key();
        this.running.put(key, runRoute);
        return this;
    }

    @Override
    public StoreRouter addCurrent(final RunRoute runRoute) {
        final RunThread thread = runRoute.thread();
        if (!this.running.containsKey(runRoute.key())) {
            this.add(runRoute);
        }
        // 不论哪种情况都需要增加线程计数
        thread.increase(false);
        return this;
    }

    @Override
    public StoreRouter remove(final RunRoute runRoute) {
        Objects.requireNonNull(runRoute);
        final String key = runRoute.key();
        this.running.remove(key);
        return this;
    }
}
