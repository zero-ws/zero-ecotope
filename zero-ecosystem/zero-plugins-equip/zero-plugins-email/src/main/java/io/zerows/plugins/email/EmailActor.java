package io.zerows.plugins.email;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.assembly.DiRegistry;
import io.zerows.sdk.plugins.AddOn;
import io.zerows.specification.configuration.HConfig;
import jakarta.inject.Provider;
import lombok.extern.slf4j.Slf4j;

@Actor(value = "email", sequence = -160)
@Slf4j
public class EmailActor extends AbstractHActor {

    public static EmailClient ofClient() {
        return EmailAddOn.of().createSingleton();
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final AddOn<EmailClient> addOn = EmailAddOn.of(vertxRef, config);
        this.vLog("[ Email ] EmailActor 初始化完成，配置：{}", config);

        final Provider<EmailClient> provider = new EmailProvider(addOn);
        DiRegistry.of().put(addOn.getKey(), provider);
        this.vLog("[ Email ] DI 提供者 Provider 注册：provider = {}, key = {}", provider, addOn.getKey());
        final EmailClient client = ofClient();
        return Future.succeededFuture(Boolean.TRUE);
    }
}
