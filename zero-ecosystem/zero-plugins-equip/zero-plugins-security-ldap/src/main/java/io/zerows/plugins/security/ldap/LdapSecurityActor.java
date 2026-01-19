package io.zerows.plugins.security.ldap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.epoch.metadata.security.SecurityConfig;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.SecurityConstant;
import io.zerows.plugins.security.SecurityProvider;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

@Actor(value = "security", sequence = -155)
public class LdapSecurityActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final SecurityConfig configLdap = SecurityActor.configOf(SecurityConstant.WALL_LDAP);
        if (Objects.isNull(configLdap)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        final boolean isEnabled = configLdap.option("enabled", Boolean.FALSE);
        if (!isEnabled) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        this.vLog("[ Security ] LDAP / SecurityActor 初始化完成，配置：{}", config);
        final SecurityProvider securityProvider = SecurityProvider.of(SecurityConstant.WALL_LDAP);
        if (Objects.isNull(securityProvider)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        securityProvider.configureProvider401(vertxRef, configLdap);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
