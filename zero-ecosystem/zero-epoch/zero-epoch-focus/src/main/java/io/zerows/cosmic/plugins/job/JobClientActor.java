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
 * 修正 JobClientActor 的启动优先级为 -188，确保它在监控之前启动，监控要调用它。
 *
 * @author lang : 2025-10-16
 */
@Actor(value = "job", sequence = -188)
@Slf4j
public class JobClientActor extends AbstractHActor {
    public static JobClient ofClient() {
        return JobClientAddOn.of().createSingleton();
    }

    @Override
    @SuppressWarnings("all")
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<JobClient> addOn = JobClientAddOn.of(vertxRef, config);
        this.vLog("[ JobClient ] JobClientActor 初始化完成，config = {}", config.options());

        final Provider<JobClient> provider = new JobClientProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ JobClient ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
