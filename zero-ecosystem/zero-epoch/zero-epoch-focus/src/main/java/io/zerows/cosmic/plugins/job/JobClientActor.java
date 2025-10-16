package io.zerows.cosmic.plugins.job;

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
 * @author lang : 2025-10-16
 */
@Actor(value = "job")
@Slf4j
public class JobClientActor extends AbstractHActor {
    @Override
    @SuppressWarnings("all")
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<JobClient> addOn = JobClientAddOn.of(vertxRef, config);
        this.vLog("[ Job ] JobClientActor 初始化完成，config = {}", config.options());

        final Provider<JobClient> provider = new JobClientProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Job ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
