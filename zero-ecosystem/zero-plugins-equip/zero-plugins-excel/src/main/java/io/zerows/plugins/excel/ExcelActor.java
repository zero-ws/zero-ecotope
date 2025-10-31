package io.zerows.plugins.excel;

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
 * @author lang : 2025-10-31
 */
@Actor("excel")
@Slf4j
public class ExcelActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<ExcelClient> addOn = ExcelAddOn.of(vertxRef, config);
        this.vLog("[ Excel ] ExcelActor 初始化完成，忽略配置");

        final Provider<ExcelClient> provider = new ExcelProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Excel ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        return Future.succeededFuture(Boolean.TRUE);
    }

    public static ExcelClient ofClient() {
        return ExcelAddOn.of().createSingleton();
    }
}
