package io.zerows.plugins.swagger;

import io.r2mo.typed.cc.Cc;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.Handler;
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

    // Knife4j 资源常量
    private static final String KNIFE4J_ENTRY = "META-INF/resources/doc.html";
    private static final String KNIFE4J_WEBJARS = "META-INF/resources/webjars";

    // 控制日志只打印一次的开关
    private static final AtomicBoolean IS_LOG_UI = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_LOG_OPEN = new AtomicBoolean(Boolean.TRUE);
    private static final AtomicBoolean IS_LOG_KNIFE = new AtomicBoolean(Boolean.TRUE);
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

        // -------------------------------------------------------------
        // 1. 🔥 必须先挂载 Knife4j (因为它有具体的路径 doc.html)
        //    如果先挂载 SwaggerUi，它的 /* 通配符会拦截掉 Knife4j 的请求
        // -------------------------------------------------------------
        this.mountKnife4j(server, vertxDoc);

        // 2. 挂载 原生 Swagger UI (包含拦截逻辑和通配符)
        this.mountSwaggerUi(server, vertxDoc);

        // 3. 挂载 OpenAPI YAML (含文件导出)
        final OpenAPI openAPI = SwaggerActor.apiOf(vertx);
        this.mountOpenApi(server, vertxDoc, openAPI);

        // 4. 挂载 Config 接口
        this.mountConfig(server, vertxDoc);
    }

    // =============================================================
    // 🔥 Knife4j 专用挂载逻辑
    // =============================================================
    private void mountKnife4j(final RunServer server, final SwaggerConfig vertxDoc) {
        final Router router = server.refRouter();
        final String version = vertxDoc.getVersion();
        final String basePath = Ut.ioPath(PATH_DOCS, version);

        // 1. 🛡️ 强制计算绝对路径 (以 / 开头)
        // 这是防止 "v3/v3" 现象的物理防线，必须保留
        final String rawYamlPath = Ut.ioPath(PATH_DOCS, version, vertxDoc.getApiDocs().getPath());
        final String fullYamlPath = rawYamlPath.startsWith("/") ? rawYamlPath : "/" + rawYamlPath;

        final String rawConfigPath = Ut.ioPath(PATH_DOCS, version, vertxDoc.getSwaggerUi().getConfigUrl());
        final String fullConfigPath = rawConfigPath.startsWith("/") ? rawConfigPath : "/" + rawConfigPath;

        // 2. 挂载静态资源 (Webjars)
        // 静态资源建议开启缓存
        final String webjarsRoute = Ut.ioPath(basePath, "webjars/*");
        router.route(webjarsRoute).handler(
            StaticHandler.create(KNIFE4J_WEBJARS).setCachingEnabled(true).setIncludeHidden(false)
        );

        // 3. 挂载 doc.html 并注入配置
        final String docRoute = Ut.ioPath(basePath, "doc.html");

        router.get(docRoute).handler(ctx -> {
            // 建议保留 ClassLoader 的双重检查 (Thread + Class)，这里简化演示
            final InputStream in = SwaggerAxis.class.getClassLoader().getResourceAsStream(KNIFE4J_ENTRY);
            if (in == null) {
                // 如果找不到资源，直接 next 让 Vert.x 处理 404，不要自己 fail
                ctx.next();
                return;
            }

            try (final InputStream fileStream = in) {
                String html = new String(fileStream.readAllBytes(), StandardCharsets.UTF_8);

                // 🔥 核心修正 1：配置脚本
                final String script = "<script type=\"text/javascript\">\n" +
                    "    window.knife4jFront = {\n" +
                    "        url: '" + fullYamlPath + "',\n" +
                    "        configUrl: '" + fullConfigPath + "',\n" +
                    "        enableSwaggerModels: true,\n" +
                    "        enableOpenApi: true,\n" +
                    "        enableFooter: false,\n" +
                    /* 🔥 强制覆盖 Knife4j 的路径拼接逻辑 */
                    "        basePath: '',\n" +  // 禁用自动前缀
                    "        apisSorter: 'alpha'\n" +
                    "    };\n" +
                    "</script>\n";

                // 🔥 核心修正 2：注入策略调整
                // 改为在 <head> 标签刚开始就注入，确保配置定义早于任何 Webjar 脚本加载
                // 同时也避免了 </head> 匹配失败的风险
                if (html.contains("<head>")) {
                    html = html.replace("<head>", "<head>" + script);
                } else {
                    // 兜底：如果 HTML 极简没有 head，就插在 html 标签后
                    html = html.replace("<html>", "<html><head>" + script + "</head>");
                }

                ctx.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8")
                    // 🔥 核心修正 3：强制禁用 doc.html 缓存
                    // 必须加！否则浏览器会一直用旧的无配置 HTML，导致你以为注入失败
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .putHeader("Pragma", "no-cache")
                    .putHeader("Expires", "0")
                    .end(html);

            } catch (final Exception e) {
                log.error("Knife4j 注入失败", e);
                ctx.fail(e);
            }
        });

        if (IS_LOG_KNIFE.getAndSet(false)) {
            log.info("{} \t\uD83D\uDDE1 Knife4j UI  : {} -> Config: `{}`", SwaggerConstant.K_PREFIX, docRoute, fullConfigPath);
        }
    }

    private void mountSwaggerUi(final RunServer server, final SwaggerConfig vertxDoc) {
        final Router router = server.refRouter();
        final String version = vertxDoc.getVersion();
        final SwaggerConfig.SwaggerUi swaggerUi = vertxDoc.getSwaggerUi();
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();

        final String basePath = Ut.ioPath(PATH_DOCS, version); // /docs/v3

        // 提前计算正确的 YAML 完整路径
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
            final List<String> possiblePaths = Arrays.asList(
                userPath + "/index.html",
                "webroot/" + userPath + "/index.html",
                "META-INF/resources/" + userPath + "/index.html",
                "META-INF/resources/webjars/swagger-ui-dist/index.html"
            );

            if (!this.tryFindAndSend(ctx, possiblePaths, swaggerUi.getConfigUrl(), fullYamlPath)) {
                // 不建议在此处 warn，因为可能是正常的 404，交给 StaticHandler 处理
                ctx.next();
            }
        });

        // --- 3. 静态资源托管 (注意：这里包含了 /*，所以必须最后挂载) ---
        final String pathRoute = Ut.ioPath(basePath, "/*");
        final StaticHandler handler = StaticHandler.create(swaggerUi.getPath());
        router.route(pathRoute).handler(handler);

        if (IS_LOG_UI.getAndSet(Boolean.FALSE)) {
            log.info("{} \t\uD83E\uDE90 Swagger UI 路径：{}", SwaggerConstant.K_PREFIX, pathRoute);
        }
    }

    private boolean tryFindAndSend(final RoutingContext ctx,
                                   final List<String> paths,
                                   final String configUrl,
                                   final String yamlPath) {
        for (final String path : paths) {
            try (final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
                if (in != null) {
                    final String html = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    final int bodyEndIndex = html.lastIndexOf("</body>");

                    if (bodyEndIndex > 0) {
                        final String hijackScript = this.generateHijackScript(configUrl, yamlPath);
                        final String patchedHtml = html.substring(0, bodyEndIndex) + hijackScript + html.substring(bodyEndIndex);
                        this.sendHtml(ctx, patchedHtml);
                        return true;
                    }
                    this.sendHtml(ctx, html);
                    return true;
                }
            } catch (final Exception e) {
                // ignore
            }
        }
        return false;
    }

    private String generateHijackScript(final String configUrl, final String yamlPath) {
        return "\n<script>\n" +
            "window.onload = function() {\n" +
            "  var ui = SwaggerUIBundle({\n" +
            "    url: '" + yamlPath + "',\n" +
            "    configUrl: '" + configUrl + "',\n" +
            "    dom_id: '#swagger-ui',\n" +
            "    deepLinking: true,\n" +
            "    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset],\n" +
            "    plugins: [SwaggerUIBundle.plugins.DownloadUrl],\n" +
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
        final String yaml = Yaml.pretty(openAPI);

        if (vertxDoc.isOutput()) {
            final String dumpDir = ".r2mo/api";
            final String dumpFile = dumpDir + "/metadata.yaml";
            final FileSystem fs = server.refVertx().fileSystem();

            fs.mkdirs(dumpDir)
                .compose(v -> fs.writeFile(dumpFile, Buffer.buffer(yaml)))
                .onSuccess(v -> {
                    if (IS_LOG_DUMP.getAndSet(Boolean.FALSE)) {
                        final String absolutePath = Paths.get(dumpFile).toAbsolutePath().toString();
                        log.info("[ XMOD ] ( Doc ) OpenAPI 规范定义已生成：{}", absolutePath);
                    }
                })
                .onFailure(err -> {
                    if (IS_LOG_DUMP.get()) {
                        log.error("[ XMOD ] ( Doc ) OpenAPI 规范生成失败", err);
                    }
                });
        }

        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();
        // FIX-BUG: 新版移除 openapi.yaml，改用 openapi.json，可兼容 Swagger UI 和 Knife4j
        //        final String metadata = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());
        //
        //        server.refRouter().route(metadata).handler(ctx -> {
        //            ctx.response()
        //                /* 🔥 关键修复 1：明确字符编码 */
        //                .putHeader(HttpHeaders.CONTENT_TYPE, "application/x-yaml; charset=utf-8")
        //                /* 🔥 关键修复 2：禁用缓存 */
        //                .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
        //                /* 🔥 关键修复 3：显式指定编码 */
        //                .end(yaml);
        //        });

        final String json = Json.pretty(openAPI);
        /* 🔥 注册 JSON 路由 */
        final String metadata = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());
        server.refRouter().route(metadata).handler(ctx -> {
            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .end(json);
        });

        if (IS_LOG_OPEN.getAndSet(Boolean.FALSE)) {
            log.info("{} \t\uD83E\uDE90 Open API 发布路径：{}", SwaggerConstant.K_PREFIX, metadata);
        }
    }

    private void mountConfig(final RunServer server, final SwaggerConfig vertxDoc) {
        final SwaggerConfig.SwaggerUi swaggerUi = vertxDoc.getSwaggerUi();
        final SwaggerConfig.ApiDocs apiDocs = vertxDoc.getApiDocs();

        /* 🔥 原始路径：/docs/v3/swagger-config */
        final String configPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), swaggerUi.getConfigUrl());

        /* 🔥 Knife4j 实际请求路径：/docs/v3/v3/api-docs/swagger-config
         *    FIX-BUG: Knife4j 内部写死了 v3/api-docs/swagger-config，所以此处没有任何办法更改
         * */
        final String knife4jConfigPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), "v3/v3/api-docs", swaggerUi.getConfigUrl());

        /* 📌 统一的处理逻辑 */
        final Handler<RoutingContext> configHandler = ctx -> {
            final JsonObject configJson = JsonObject.mapFrom(swaggerUi);
            final String fullYamlPath = Ut.ioPath(PATH_DOCS, vertxDoc.getVersion(), apiDocs.getPath());

            /* 🛡️ 关键：返回绝对路径，防止 Knife4j 再次拼接 */
            configJson.put("url", fullYamlPath);
            configJson.put("urls", null);  // 清空多文档配置

            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")  // 🔥 禁用缓存
                .end(configJson.encode());
        };

        server.refRouter().route(configPath).handler(configHandler);
        server.refRouter().route(knife4jConfigPath).handler(configHandler);
    }
}