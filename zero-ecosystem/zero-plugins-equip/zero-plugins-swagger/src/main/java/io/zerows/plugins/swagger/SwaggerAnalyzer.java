package io.zerows.plugins.swagger;

import io.r2mo.openapi.metadata.DocApi;
import io.r2mo.openapi.metadata.DocMeta;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.WebEvent;
import jakarta.ws.rs.HttpMethod;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
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

        final Set<WebEvent> events = ACTOR.value().getEvents();

        final List<DocApi> apis = events.stream().map(event -> {
                final DocApi api = new DocApi();
                api.path(event.getPath());
                api.invoker(event.getAction());

                // --- 新增：Http Method 解析适配 ---
                // 将 Vert.x 的 HttpMethod 转换为 Jakarta 的 HttpMethod 注解实例
                if (event.getMethod() != null) {
                    api.method(adaptMethod(event.getMethod()));
                }
                // ------------------------------------

                return api;
            })// 2. 【核心修改】按 path 字典序排序 (如果 path 相同，建议按 method 排序以保证稳定性)
            .sorted(
                Comparator.comparing((DocApi api) -> api.path())
                    .thenComparing(api -> api.method().value())
            )
            .toList();

        DocMeta.load(openAPI, apis);
        return openAPI;
    }

    /**
     * 适配器：将 Vert.x HttpMethod 包装为 JAX-RS HttpMethod 注解接口
     */
    private static HttpMethod adaptMethod(final io.vertx.core.http.HttpMethod vertxMethod) {
        return new HttpMethod() {
            @Override
            public String value() {
                // 返回标准的方法名字符串 (GET, POST, etc.)
                return vertxMethod.name();
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                // 必须返回对应的注解类型，否则部分反射工具可能会报错
                return HttpMethod.class;
            }

            @Override
            public String toString() {
                return vertxMethod.name();
            }
        };
    }
}