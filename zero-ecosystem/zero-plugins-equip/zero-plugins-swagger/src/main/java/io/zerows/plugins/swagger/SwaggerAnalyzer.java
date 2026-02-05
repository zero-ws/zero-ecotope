package io.zerows.plugins.swagger;

import io.r2mo.openapi.metadata.DocApi;
import io.r2mo.openapi.metadata.DocExtension;
import io.r2mo.openapi.metadata.DocMeta;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.WebEvent;
import jakarta.ws.rs.HttpMethod;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class SwaggerAnalyzer {
    private static final OCacheActor ACTOR = OCacheActor.of();

    static OpenAPI compile(final SwaggerConfig doc) {
        // =============================================================
        // 步骤 1：初始化 OpenAPI 容器与基础信息
        // =============================================================
        final OpenAPI openAPI = new OpenAPI();
        openAPI.setComponents(new Components());
        openAPI.setPaths(new Paths());

        final SwaggerConfig.ApiDocs ymOpen = doc.getApiDocs();
        openAPI.setInfo(new Info()
            .title(ymOpen.getTitle())
            .version(ymOpen.getVersion())
            .description(ymOpen.getDescription())
        );

        // =============================================================
        // 步骤 2：准备工作 (提取公共逻辑)
        // =============================================================
        // 2.1 获取全局路由事件集合
        final Set<WebEvent> events = ACTOR.value().getEvents();

        // 2.2 获取扩展处理器，并判断是否存在（避免在流中重复进行 null 检查）
        final DocExtension extension = DocExtension.of();
        final boolean hasExtension = Objects.nonNull(extension);

        // 2.3 定义排序规则：复用 Comparator，避免写两遍
        // 逻辑：优先按 URL 路径(Path) 字典序排序；如果路径相同，则按 HTTP 方法(Method) 排序
        // 修正后的 Comparator
        final Comparator<DocApi> apiSorter = Comparator
            // 1. 显式指定 (DocApi api)，消除方法引用歧义和类型推断问题
            .comparing((DocApi api) -> api.path())

            // 2. 这里的 api 类型现在可以被正确推断了
            // 注意：如果 api.method() 可能为 null，这里运行时会报空指针，建议加个防护
            .thenComparing(api -> {
                if (api.method() == null) {
                    return ""; // 防止 NPE，空值排最前或最后
                }
                return api.method().value();
            });

        // =============================================================
        // 步骤 3：核心流处理 —— 数据清洗与分流 (Partitioning)
        // =============================================================
        // 这里的目标是：将原始 WebEvent 转换为 DocApi，并直接根据“是否需要扩展”一分为二。
        // Map 的 Key：Boolean
        //      - true  : 表示该 API 匹配到了扩展规则 (需要后续特殊处理)
        //      - false : 表示该 API 是普通标准接口
        final Map<Boolean, List<DocApi>> partitionedApis = events.stream()
            // 3.1 [转换]：将 Vert.x 的 WebEvent 转为文档专用的 DocApi 对象
            .map(event -> {
                final DocApi api = new DocApi();
                api.path(event.getPath());
                api.invoker(event.getAction());

                // 适配 HTTP Method (从 Vert.x 枚举转为 Jakarta 注解)
                if (event.getMethod() != null) {
                    api.method(adaptMethod(event.getMethod()));
                }
                return api;
            })
            // 3.2 [分流]：使用 partitioningBy 替代原来的 filter 副作用
            // 如果 hasExtension 为 true 且 extension.isMatch(api) 返回 true，则分到 key=true 的组
            .collect(Collectors.partitioningBy(api ->
                hasExtension && extension.isMatch(api)
            ));

        // =============================================================
        // 步骤 4：处理普通 API (Standard APIs)
        // =============================================================
        // 从分流结果中取出 key=false 的列表
        final List<DocApi> standardApis = partitionedApis.get(false);

        if (standardApis != null && !standardApis.isEmpty()) {
            // 原地排序 (性能优于流式 sorted)
            standardApis.sort(apiSorter);
            // 加载到 OpenAPI 对象中
            DocMeta.load(openAPI, standardApis);
        }

        // =============================================================
        // 步骤 5：处理扩展 API (Extended APIs)
        // =============================================================
        // 从分流结果中取出 key=true 的列表
        final List<DocApi> extensionSourceApis = partitionedApis.get(true);

        if (extensionSourceApis != null && !extensionSourceApis.isEmpty()) {
            // 扩展接口的处理逻辑往往是 "一对多" (一个定义生成多个实际接口)，所以需要 flatMap
            final List<DocApi> compiledExtensions = extensionSourceApis.stream()
                // 5.1 编译扩展：调用扩展逻辑生成最终的 API 列表
                .map(apiDoc -> extension.compile(openAPI, apiDoc))
                // 5.2 扁平化：将 List<List<DocApi>> 铺平为 Stream<DocApi>
                .flatMap(Collection::stream)
                // 5.3 排序：使用前面定义好的排序器
                .sorted(apiSorter)
                // 5.4 收集结果
                .toList(); // JDK 16+ 写法，旧版可用 .collect(Collectors.toList())

            // 加载扩展后的 API
            DocMeta.load(openAPI, compiledExtensions);
        }

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