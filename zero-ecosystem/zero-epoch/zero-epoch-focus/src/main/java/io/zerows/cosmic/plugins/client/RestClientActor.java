package io.zerows.cosmic.plugins.client;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.epoch.configuration.ConfigNorm;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Actor(value = "rest", sequence = -188, configured = false)
public class RestClientActor extends AbstractHActor {
    public static RestClient ofClient() {
        return RestClientAddOn.of().createSingleton();
    }

    @Override
    protected Future<Boolean> startAsync(HConfig config, final Vertx vertxRef) {
        if (Objects.isNull(config)) {
            config = new ConfigNorm();
        }
        final AddOn<RestClient> addOn = RestClientAddOn.of(vertxRef, config);
        this.vLog("[ REST ] RestClientActor 初始化完成，config = {}", config.options());

        final Provider<RestClient> provider = new RestClientProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ REST ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
