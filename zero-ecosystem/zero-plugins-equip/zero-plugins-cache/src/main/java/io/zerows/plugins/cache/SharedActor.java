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
 * @author lang : 2025-10-15
 */
@Actor(value = "SHARE", configured = false)
@Slf4j
public class SharedActor extends AbstractHActor {

    public static SharedClient ofClient() {
        return SharedAddOn.of().createSingleton();
    }

    @Override
    @SuppressWarnings("all")
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<SharedClient> addOn = SharedAddOn.of(vertxRef, config);
        this.vLog("[ Shared ] SharedActor 初始化完成，忽略配置");

        final Provider<SharedClient> provider = new SharedProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Shared ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
