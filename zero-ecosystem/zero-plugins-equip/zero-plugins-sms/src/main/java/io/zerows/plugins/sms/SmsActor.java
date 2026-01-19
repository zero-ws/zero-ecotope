package io.zerows.plugins.sms;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

@Actor(value = "sms", sequence = -160)
@Slf4j
public class SmsActor extends AbstractHActor {
    public static SmsClient ofClient() {
        return SmsAddOn.of().createSingleton();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<SmsClient> addOn = SmsAddOn.of(vertxRef, config);
        this.vLog("[ SMS ] SmsActor 初始化完成，配置：{}", config);

        final Provider<SmsClient> provider = new SmsProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ SMS ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());

        SmsManager.of().configOf(vertxRef, config);
        this.vLog("[ SMS ] ----> 已启用短信服务！");
        return Future.succeededFuture(Boolean.TRUE);
    }
}