package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.module.AbstractHActor;
import io.zerows.epoch.annotations.Actor;
import io.zerows.plugins.swagger.management.OCacheSwagger;
import io.zerows.specification.configuration.HConfig;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author lang : 2025-10-17
 */
@Actor(value = "swagger")
@Slf4j
public class SwaggerActor extends AbstractHActor {

    @Override
    protected Future<Boolean> startAsync(final HConfig config, final Vertx vertxRef) {
        this.vLog("[ Swagger ] SwaggerActor 正在扫描接口类，vertx = {}", vertxRef.hashCode());

        // Step1: 扫描接口类
        final Set<Class<?>> handlerClasses = SwaggerScanner.scan();

        // Step2: 生成 OpenAPI 文档
        final OpenAPI openAPI = SwaggerOpenAPI.generate(handlerClasses);

        // Step3: 存入缓存
        final SwaggerData swaggerData = new SwaggerData(handlerClasses, openAPI);
        OCacheSwagger.of(HPI.findBundle(this.getClass())).add(swaggerData);
        //键分离 vertx  命名改了
        this.vLog("[ Swagger ] ✅ SwaggerActor 已成功扫描完成，扫描到 {} 个处理器类", handlerClasses.size());
        return Future.succeededFuture(Boolean.TRUE);
    }
}
