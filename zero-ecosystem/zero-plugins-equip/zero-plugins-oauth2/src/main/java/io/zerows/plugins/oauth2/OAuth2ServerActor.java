package io.zerows.plugins.oauth2;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;

import java.util.Objects;

@Actor(value = "oauth2", sequence = -160)
public class OAuth2ServerActor extends AbstractHActor {
    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        if (Objects.isNull(config)) {
            return Future.succeededFuture(Boolean.TRUE);
        }

        this.vLog("[ OAuth2 ] OAuth2管理器初始化完成，配置：{}", config.options());

        OAuth2Manager.of().configOf(vertxRef, config);
        this.vLog("[ OAuth2 ] OAuth2管理配置已加载完成！！");
        return Future.succeededFuture(Boolean.TRUE);
    }
}
