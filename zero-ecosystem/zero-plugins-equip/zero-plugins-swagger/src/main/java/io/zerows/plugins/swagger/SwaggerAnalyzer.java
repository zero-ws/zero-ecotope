package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.WebEvent;

import java.util.Set;

class SwaggerAnalyzer {
    private static final OCacheActor ACTOR = OCacheActor.of();

    static OpenAPI compile(final SwaggerConfig doc) {
        final OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(new Components());
        openAPI.setPaths(new Paths());

        // 1. 基础信息
        final SwaggerConfig.ApiDocs ymOpen = doc.getApiDocs();
        final Info info = new Info()
            .title(ymOpen.getTitle())
            .version(ymOpen.getVersion())
            .description(ymOpen.getDescription());
        openAPI.setInfo(info);

        // 2. 初始化解析策略
        // 顺序很重要：先 JAX-RS 生成骨架，再 Swagger 进行装修
        final DocSpec jaxrsSpec = DocSpec.of(DocSpecJAXRS::new);
        final DocSpec swaggerSpec = DocSpec.of(DocSpecSwagger::new);

        // 3. 扫描所有 WebEvent
        final Set<WebEvent> events = ACTOR.value().getEvents();
        for (final WebEvent event : events) {
            // 第一步：JAX-RS 基础解析
            jaxrsSpec.compile(openAPI, event);
            // 第二步：Swagger 注解增强
            swaggerSpec.compile(openAPI, event);
        }

        return openAPI;
    }
}