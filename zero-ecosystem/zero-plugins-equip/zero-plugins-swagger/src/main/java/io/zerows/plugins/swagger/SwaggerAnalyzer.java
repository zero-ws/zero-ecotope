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

        // ==========================================
        // 【核心修改点】必须在此处加载 Schema
        // ==========================================
        // 这会将 openapi/components/schemas 下的定义
        // 注册到 openAPI.components 中，解决 $ref 找不到的问题
        LoaderSchema.load(openAPI);

        // 2. 初始化解析策略
        final DocSpec jaxrsSpec = DocSpec.of(DocSpecJAXRS::new);
        final DocSpec swaggerSpec = DocSpec.of(DocSpecSwagger::new);
        final DocSpec openApiSpec = DocSpec.of(DocSpecOpenAPI::new);

        // 3. 扫描所有 WebEvent
        final Set<WebEvent> events = ACTOR.value().getEvents();
        for (final WebEvent event : events) {
            // 3.1 生成骨架
            jaxrsSpec.compile(openAPI, event);

            // 3.2 注解增强
            swaggerSpec.compile(openAPI, event);

            // 3.3 文档覆盖 (Operation)
            openApiSpec.compile(openAPI, event);
        }

        return openAPI;
    }
}