package io.zerows.plugins.security.sms;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Actor(value = "sms", sequence = -155)
@Slf4j
public class SmsAuthActor extends AbstractHActor {

    private static SmsAuthConfig INSTANCE;

    public static SmsAuthConfig configOf() {
        return INSTANCE;
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final SecurityConfig securityConfig = SecurityActor.configOf("captcha-sms");
        if (Objects.isNull(securityConfig)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final JsonObject options = securityConfig.options();
        if (Ut.isNil(options)) {
            log.warn("[ SMS ] SMS / SecurityActor 配置为空，跳过初始化。");
            return Future.succeededFuture(Boolean.TRUE);
        }
        INSTANCE = Ut.deserialize(options, SmsAuthConfig.class);
        this.vLog("[ SMS ] SMS / SecurityActor 初始化完成，配置：{}", options);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
