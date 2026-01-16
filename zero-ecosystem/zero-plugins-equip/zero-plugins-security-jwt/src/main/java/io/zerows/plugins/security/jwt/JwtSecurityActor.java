package io.zerows.plugins.security.jwt;

import io.r2mo.jaas.token.TokenBuilderManager;
import io.r2mo.jaas.token.TokenType;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

@Actor(value = "security", sequence = -155)
public class JwtSecurityActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final SecurityConfig configJwt = SecurityActor.configJwt();
        if (Objects.isNull(configJwt)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final boolean isEnabled = configJwt.option("enabled", Boolean.FALSE);
        if (!isEnabled) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        this.vLog("[ Security ] JwtSecurityActor 初始化完成，配置：{}", config);

        TokenBuilderManager.of().registry(TokenType.JWT, JwtTokenBuilder::new);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
