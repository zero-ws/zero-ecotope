package io.zerows.plugins.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.WebEvent;

import java.util.Set;

class SwaggerAnalyzer {
    private static final OCacheActor ACTOR = OCacheActor.of();

    static OpenAPI compile(final SwaggerConfig doc) {
        final OpenAPI openAPI = new OpenAPI();
        // 添加基础信息
        final SwaggerConfig.ApiDocs ymOpen = doc.getApiDocs();
        compile(ymOpen, openAPI);

        // 接口信息提取
        final Set<WebEvent> events = ACTOR.value().getEvents();
        for (final WebEvent event : events) {
            
        }

        // 返回最终结果
        return openAPI;
    }

    private static void compile(final SwaggerConfig.ApiDocs doc, final OpenAPI openAPI) {
        final Info infoDoc = new Info();
        infoDoc.title(doc.getTitle());
        infoDoc.version(doc.getVersion());
        infoDoc.description(doc.getDescription());
        openAPI.setInfo(infoDoc);
    }
}
