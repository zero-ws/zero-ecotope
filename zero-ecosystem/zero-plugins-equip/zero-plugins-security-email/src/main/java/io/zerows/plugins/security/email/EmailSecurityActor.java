package io.zerows.plugins.security.email;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.email.metadata.YmSecurityEmail;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Actor(value = "email", sequence = -155)
@Slf4j
public class EmailSecurityActor extends AbstractHActor {

    private static YmSecurityEmail INSTANCE;

    public static YmSecurityEmail configOf() {
        return INSTANCE;
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final SecurityConfig securityConfig = SecurityActor.configOf("captcha-email");
        if (Objects.isNull(securityConfig)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final JsonObject options = securityConfig.options();
        INSTANCE = Ut.deserialize(options, YmSecurityEmail.class);
        this.vLog("[ Email ] Email / SecurityActor 初始化完成，配置：{}", options);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
