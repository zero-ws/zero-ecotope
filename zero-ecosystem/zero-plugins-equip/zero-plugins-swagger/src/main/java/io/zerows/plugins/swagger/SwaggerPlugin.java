package io.zerows.plugins.swagger;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class SwaggerPlugin {

    public static void mount(Router router, Vertx vertx) {
        // Step1: 扫描接口类
        var handlerClasses = SwaggerAnnotationScanner.scan();

        // Step2: 生成 OpenAPI 文档
        var openAPI = OpenApiGenerator.generate(handlerClasses);

        // Step3: 构造路由并挂载
        SwaggerRouteBuilder.mount(router, openAPI);
    }
}
