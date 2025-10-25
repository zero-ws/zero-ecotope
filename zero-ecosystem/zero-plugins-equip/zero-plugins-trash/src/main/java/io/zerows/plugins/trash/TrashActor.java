package io.zerows.plugins.trash;

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
 * @author lang : 2025-10-17
 */
@Actor(value = "trash")
@Slf4j
public class TrashActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<TrashClient> addOn = TrashAddOn.of(vertxRef, config);
        this.vLog("[ Trash ] TrashActor 初始化完成，配置：{}", config);

        final Provider<TrashClient> provider = new TrashProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Trash ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }

    public static TrashClient ofDefault() {
        return TrashAddOn.of().createSingleton();
    }
}
