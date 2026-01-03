package io.zerows.plugins.cache;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2026-01-02
 */
@Actor(value = "cache", sequence = -188)
@Slf4j
public class CachedActor extends AbstractHActor {

    public static CachedClient ofClient() {
        return CachedAddOn.of().createSingleton();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<CachedClient> addOn = CachedAddOn.of(vertxRef, config);
        this.vLog("[ Cached ] SharedActor 初始化完成，忽略配置");

        final Provider<CachedClient> provider = new CachedProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Cached ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
