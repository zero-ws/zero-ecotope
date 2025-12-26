package io.zerows.plugins.integration.sms;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.plugins.integration.sms.SmsClient;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

@Actor(value = "sms")
@Slf4j
public class SmsActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<SmsClient> addOn = SmsAddOn.of(vertxRef, config);
        this.vLog("[ Sms ] SmsActor 初始化完成，配置：{}", config);

        final Provider<SmsClient> provider = new SmsProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Sms ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());

        return Future.succeededFuture(Boolean.TRUE);
    }

    public static SmsClient ofClient() {
        return SmsAddOn.of().createSingleton();
    }
}