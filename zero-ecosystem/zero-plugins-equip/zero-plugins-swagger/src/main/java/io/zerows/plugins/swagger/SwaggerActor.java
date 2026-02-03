package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.specification.configuration.HConfig;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "vertxdoc")
@Slf4j
public class SwaggerActor extends AbstractHActor {
    private static final SwaggerManager MANAGER = SwaggerManager.of();

    static SwaggerConfig registryOf(final Vertx instance) {
        return MANAGER.registryOf(instance);
    }

    static OpenAPI apiOf(final Vertx instance) {
        return MANAGER.apiOf(instance);
    }

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        final SwaggerConfig vertxDoc = this.loadConfig(config);
        if (Objects.isNull(vertxDoc)) {
            return Future.succeededFuture(Boolean.TRUE);
        }
        if (!vertxDoc.isEnabled()) {
            return Future.succeededFuture(Boolean.TRUE);
        }


        // 配置注册
        this.vLog("[ Swagger ] SwaggerActor 初始化完成，配置：{}", config);
        MANAGER.registryOf(vertxRef, vertxDoc);


        // 元数据扫描
        return Future.succeededFuture(Boolean.TRUE);
    }

    private SwaggerConfig loadConfig(final HConfig config) {
        if (Objects.isNull(config)) {
            return null;
        }
        final JsonObject options = config.options();
        return Ut.deserialize(options, SwaggerConfig.class);
    }
}
