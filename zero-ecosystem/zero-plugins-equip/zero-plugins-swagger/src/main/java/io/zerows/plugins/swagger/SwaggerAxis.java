package io.zerows.plugins.swagger;

import io.r2mo.typed.cc.Cc;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
class SwaggerAxis implements Axis {
    private static final Cc<String, Axis> CC_SWAGGER = Cc.openThread();
    private static final String PATH_DOCS = "/docs";
    // 控制日志只打印一次的开关
    private static final AtomicBoolean IS_LOG_UI = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_LOG_OPEN = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_LOG_DUMP = new AtomicBoolean(Boolean.TRUE);

    private SwaggerAxis() {
    }

    static Axis of() {
        return CC_SWAGGER.pick(SwaggerAxis::new, SwaggerAxis.class.getName());
    }

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        final Vertx vertx = server.refVertx();
        final SwaggerConfig vertxDoc = SwaggerActor.registryOf(vertx);

        // 1. 挂载 UI (包含拦截逻辑)
        this.mount(server, vertxDoc);

        // 2. 挂载 OpenAPI YAML (含文件导出)
        final OpenAPI openAPI = SwaggerActor.apiOf(vertx);
        this.mountOpenApi(server, vertxDoc, openAPI);

        // 3. 挂载 Config 接口
        this.mountConfig(server, vertxDoc);
    }

    private void mount(final RunServer server, final SwaggerConfig vertxDoc) {
        final Router router = server.refRouter();
        final String version = vertxDoc.getVersion();
        final SwaggerConfig.SwaggerUi swaggerUi = vertxDoc.getSwaggerUi();
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();

        final String basePath = Ut.ioPath(PATH_DOCS, version); // /docs/v3

        // 提前计算正确的 YAML 完整路径，用于注入到 HTML 中
        final String fullYamlPath = Ut.ioPath(PATH_DOCS, version, apiDocs.getPath());

        // --- 1. 强制重定向 ---
        router.route(basePath + "/").handler(ctx -> {
            final String redirectUrl = basePath + "/index.html?configUrl=" + swaggerUi.getConfigUrl();
            ctx.response().putHeader(HttpHeaders.LOCATION, redirectUrl).setStatusCode(302).end();
        });

        // --- 2. 核心：拦截 index.html 并修改 ---
        final String indexRoutePath = Ut.ioPath(basePath, "index.html");

        router.get(indexRoutePath).handler(ctx -> {
            final String userPath = swaggerUi.getPath();
            // 定义可能的 ClassPath 路径列表
            final List<String> possiblePaths = Arrays.asList(
                userPath + "/index.html",                                  // 1. 直接路径
                "webroot/" + userPath + "/index.html",                     // 2. webroot 下
                "META-INF/resources/" + userPath + "/index.html",          // 3. Maven 资源常用路径
                "META-INF/resources/webjars/swagger-ui-dist/index.html"    // 4. Webjars 默认路径
            );

            // 尝试读取并注入覆盖脚本
            if (!this.tryFindAndSend(ctx, possiblePaths, swaggerUi.getConfigUrl(), fullYamlPath)) {
                log.warn("Swagger拦截器：未能在 ClassPath 中找到 index.html，无法注入配置。将使用默认静态资源。");
                ctx.next();
            }
        });

        // --- 3. 静态资源托管 ---
        final String pathRoute = Ut.ioPath(basePath, "/*");
        final StaticHandler handler = StaticHandler.create(swaggerUi.getPath());
        router.route(pathRoute).handler(handler);

        if (IS_LOG_OPEN.getAndSet(Boolean.FALSE)) {
            log.info("{} \t\uD83E\uDE90 Swagger UI 路径：{}", SwaggerConstant.K_PREFIX, pathRoute);
        }
    }

    /**
     * 尝试遍历路径读取文件，如果读到则修改并返回 true，否则返回 false
     */
    private boolean tryFindAndSend(final RoutingContext ctx,
                                   final List<String> paths,
                                   final String configUrl,
                                   final String yamlPath) {
        for (final String path : paths) {
            try (final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
                if (in != null) {
                    final String html = new String(in.readAllBytes(), StandardCharsets.UTF_8);

                    // 查找 </body> 标签
                    final int bodyEndIndex = html.lastIndexOf("</body>");

                    if (bodyEndIndex > 0) {
                        log.info("Swagger拦截器：成功在 {} 中注入启动脚本", path);

                        // 生成覆盖脚本
                        final String hijackScript = this.generateHijackScript(configUrl, yamlPath);

                        // 插入到 </body> 之前
                        final String patchedHtml = html.substring(0, bodyEndIndex) + hijackScript + html.substring(bodyEndIndex);

                        this.sendHtml(ctx, patchedHtml);
                        return true;
                    }

                    // 如果找不到 </body>，说明文件结构极其特殊
                    log.error("Swagger拦截器：严重失败！无法修改 index.html (未找到 </body> 标签)。");
                    this.sendHtml(ctx, html);
                    return true;
                }
            } catch (final Exception e) {
                // ignore and try next path
            }
        }
        return false;
    }

    /**
     * 生成用于覆盖 window.onload 的 JavaScript 脚本
     */
    private String generateHijackScript(final String configUrl, final String yamlPath) {
        return "\n<script>\n" +
            "window.onload = function() {\n" +
            "  // SwaggerAxis 自动注入：强制覆盖初始化逻辑\n" +
            "  var ui = SwaggerUIBundle({\n" +
            "    url: '" + yamlPath + "',\n" +      // 默认 YAML 地址
            "    configUrl: '" + configUrl + "',\n" + // 动态配置接口
            "    dom_id: '#swagger-ui',\n" +
            "    deepLinking: true,\n" +
            "    presets: [\n" +
            "      SwaggerUIBundle.presets.apis,\n" +
            "      SwaggerUIStandalonePreset\n" +
            "    ],\n" +
            "    plugins: [\n" +
            "      SwaggerUIBundle.plugins.DownloadUrl\n" +
            "    ],\n" +
            "    layout: 'StandaloneLayout',\n" +
            "    queryConfigEnabled: true\n" +
            "  });\n" +
            "  window.ui = ui;\n" +
            "};\n" +
            "</script>\n";
    }

    private void sendHtml(final RoutingContext ctx, final String content) {
        ctx.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8")
            .end(content);
    }

    private void mountOpenApi(final RunServer server, final SwaggerConfig vertxDoc, final OpenAPI openAPI) {
        // 1. 生成标准 YAML 字符串
        final String yaml = Yaml.pretty(openAPI);
        
        // 确保目录存在，然后写入文件
        if (vertxDoc.isOutput()) {
            // 2. [新增] 启动时 Dump 到磁盘 (.r2mo/api/metadata.yaml)
            final String dumpDir = ".r2mo/api";
            final String dumpFile = dumpDir + "/metadata.yaml";
            final FileSystem fs = server.refVertx().fileSystem();

            fs.mkdirs(dumpDir)
                .compose(v -> fs.writeFile(dumpFile, Buffer.buffer(yaml)))
                .onSuccess(v -> {
                    // 使用 AtomicBoolean 确保只打印一次
                    if (IS_LOG_DUMP.getAndSet(Boolean.FALSE)) {
                        final String absolutePath = Paths.get(dumpFile).toAbsolutePath().toString();
                        log.info("[ XMOD ] ( Doc ) OpenAPI 规范定义已生成：{}", absolutePath);
                    }
                })
                .onFailure(err -> {
                    // 错误日志通常建议保留，或者也用 AtomicBoolean 控制
                    if (IS_LOG_DUMP.get()) {
                        log.error("[ XMOD ] ( Doc ) OpenAPI 规范生成失败", err);
                    }
                });
        }

        // 3. 挂载 HTTP 接口
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();
        final String metadata = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());

        server.refRouter().route(metadata).handler(ctx -> {
            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/yaml")
                .end(yaml);
        });

        if (IS_LOG_UI.getAndSet(Boolean.FALSE)) {
            log.info("{} \t\uD83E\uDE90 Open API 发布路径：{}", SwaggerConstant.K_PREFIX, metadata);
        }
    }

    private void mountConfig(final RunServer server, final SwaggerConfig vertxDoc) {
        final SwaggerConfig.SwaggerUi swaggerUi = vertxDoc.getSwaggerUi();
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();

        final String configPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), swaggerUi.getConfigUrl());

        server.refRouter().route(configPath).handler(ctx -> {
            final JsonObject configJson = JsonObject.mapFrom(swaggerUi);
            final String fullYamlPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());
            configJson.put("url", fullYamlPath);

            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .end(configJson.encode());
        });
    }
}