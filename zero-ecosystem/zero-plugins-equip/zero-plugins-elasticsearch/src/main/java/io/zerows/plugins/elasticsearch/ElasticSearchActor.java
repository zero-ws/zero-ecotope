package io.zerows.plugins.elasticsearch;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

@Actor(value = "elasticsearch")
@Slf4j
public class ElasticSearchActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<ElasticSearchClient> addOn = ElasticSearchAddon.of(vertxRef, config);
        this.vLog("[ ElasticSearch ] ElasticSearchActor 初始化完成，配置：{}", config);

        final Provider<ElasticSearchClient> provider = new ElasticSearchProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ ElasticSearch ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());

        return Future.succeededFuture(Boolean.TRUE);
    }

    public static ElasticSearchClient ofClient() {
        return ElasticSearchAddon.of().createSingleton();
    }
}