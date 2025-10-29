package io.zerows.plugins.security;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;

/**
 * @author lang : 2025-10-27
 */
@Actor(value = "SECURITY")
public class SecurityActor extends AbstractHActor {
    private final SecurityManager manager = SecurityManager.of();

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        // 先注册配置信息
        this.manager.registryConfiguration(config);

        return Future.succeededFuture(Boolean.TRUE);
    }

    public void registrySecurity(final HConfig config, final String appId) {
        this.manager.registryConfiguration(config, appId);
    }
}
