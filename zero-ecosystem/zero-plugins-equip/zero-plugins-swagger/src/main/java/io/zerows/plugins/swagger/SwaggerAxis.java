package io.zerows.plugins.swagger;

import io.r2mo.typed.cc.Cc;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
class SwaggerAxis implements Axis {
    private static final Cc<String, Axis> CC_SWAGGER = Cc.openThread();
    private static final String PATH_DOCS = "/docs";
    private static final AtomicBoolean IS_LOG_UI = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_LOG_OPEN = new AtomicBoolean(Boolean.TRUE);

    private SwaggerAxis() {
    }

    static Axis of() {
        return CC_SWAGGER.pick(SwaggerAxis::new, SwaggerAxis.class.getName());
    }

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        final Vertx vertx = server.refVertx();
        // 基础配置
        final SwaggerConfig vertxDoc = SwaggerActor.registryOf(vertx);

        // 1. 挂载 UI 静态资源 (含重定向逻辑)
        this.mount(server, vertxDoc);

        // 2. 挂载 OpenAPI YAML 定义
        final OpenAPI openAPI = SwaggerActor.apiOf(vertx);
        this.mountOpenApi(server, vertxDoc, openAPI);

        // 3. [步骤 1: 追加] 挂载 Swagger UI 动态配置文件接口
        this.mountConfig(server, vertxDoc);
    }

    private void mount(final RunServer server, final SwaggerConfig vertxDoc) {
        final Router router = server.refRouter();

        // Static UI
        final String version = vertxDoc.getVersion();
        final SwaggerConfig.SwaggerUi swaggerUi = vertxDoc.getSwaggerUi();

        // 基础路径，例如: /docs/v3
        final String basePath = Ut.ioPath(PATH_DOCS, version);
        // 静态资源路由: /docs/v3/*
        final String pathRoute = Ut.ioPath(basePath, "/*");

        // [步骤 2: 追加] 强制重定向
        // 当访问 /docs/v3/ 时，自动带上 ?configUrl=swagger-config 参数
        // 这样前端才知道去哪里加载你的配置和 YAML 地址
        router.route(basePath + "/").handler(ctx -> {
            // 指向我们下面 mountConfig 生成的接口
            final String redirectUrl = basePath + "/index.html?configUrl=" + swaggerUi.getConfigUrl();
            ctx.response()
                .putHeader(HttpHeaders.LOCATION, redirectUrl)
                .setStatusCode(302)
                .end();
        });

        final StaticHandler handler = StaticHandler.create(swaggerUi.getPath());
        router.route(pathRoute).handler(handler);

        if (IS_LOG_OPEN.getAndSet(Boolean.FALSE)) {
            log.info("{} \t\uD83E\uDE90 Swagger UI 发布路径：{}", SwaggerConstant.K_PREFIX, pathRoute + "/" + swaggerUi.getPath());
        }
    }

    private void mountOpenApi(final RunServer server, final SwaggerConfig vertxDoc, final OpenAPI openAPI) {
        // Serve YAML
        final String yaml = Yaml.pretty(openAPI);
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();
        // -- /docs/v3/openapi.yaml
        final String metadata = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());
        final Router router = server.refRouter();
        router.route(metadata).handler(ctx -> {
            final HttpServerResponse response = ctx.response();
            response.putHeader(HttpHeaders.CONTENT_TYPE, "application/yaml");
            response.end(yaml);
        });
        if (IS_LOG_UI.getAndSet(Boolean.FALSE)) {
            log.info("{} \t\uD83E\uDE90 Open API 发布路径：{}", SwaggerConstant.K_PREFIX, metadata);
        }
    }

    // [步骤 1: 实现] 生成供前端读取的 Config JSON
    private void mountConfig(final RunServer server, final SwaggerConfig vertxDoc) {
        final SwaggerConfig.SwaggerUi swaggerUi = vertxDoc.getSwaggerUi();
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();

        // 接口地址: /docs/v3/swagger-config
        final String configPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), swaggerUi.getConfigUrl());

        final Router router = server.refRouter();
        router.route(configPath).handler(ctx -> {
            // 1. 把你的 SwaggerUi 配置对象转成 JSON (应用 docExpansion 等配置)
            final JsonObject configJson = JsonObject.mapFrom(swaggerUi);

            // 2. [解决问题1的关键] 强制覆盖 url 字段，指向你的 openapi.yaml
            final String fullYamlPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());
            configJson.put("url", fullYamlPath);

            // 3. 返回 JSON
            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .end(configJson.encode());
        });
    }
}