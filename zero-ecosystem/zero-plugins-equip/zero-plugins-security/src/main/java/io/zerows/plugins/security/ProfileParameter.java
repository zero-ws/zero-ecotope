package io.zerows.plugins.security;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.epoch.metadata.KView;

import java.util.Objects;

/**
 * @author lang : 2025-10-30
 */
class ProfileParameter {
    /**
     * 静态授权参数构造器，这个方法主要用于 403 授权的动态构造，部分信息是存储在 Zero 系统中的，此处的参数可用来提取 403 所需的所有基本信息，
     * 资源的标识包括
     * <pre>
     *     1. HTTP 方法：GET, DELETE, POST, PUT
     *     2. 原始 URI
     * </pre>
     * 此处有针对 Pattern 路径计算的处理逻辑，例如注册的 URI 是 /api/mock/:name，那么实际请求的路径可能是 /api/mock/lang，这种模式下
     * 授权部分会分为两个级别
     * <pre>
     *     1. 原始 URI 基础授权，直接针对 /api/mock/:name 进行权限控制
     *     2. 实际请求 URI 的权限控制，针对 /api/mock/lang（数据域部分）未来版本可进行细粒度控制
     * </pre>
     * 标准化之后的数据格式
     * <pre>
     *     {
     *         // ... User Principal Original Data
     *         "metadata": {
     *             "uri": "/api/mock/:name",
     *             "uriRequest": "/api/mock/lang",
     *             "method": "GET",
     *             "view": {
     *                 "view": "视图名称",
     *                 "position": "视图位置说明（视图分组）"
     *             }
     *         },
     *         "headers": {
     *         }
     *     }
     * </pre>
     *
     * @param context 路由上下文
     * @return 授权参数
     */
    static JsonObject build(final RoutingContext context) {
        final User user = context.user();
        final JsonObject normalized;
        if (Objects.isNull(user)) {
            normalized = new JsonObject();
        } else {
            // Keep the original workflow
            normalized = user.principal().copy();
        }
        final HttpServerRequest request = context.request();
        /*
         * Build metadata
         */
        final JsonObject metadata = new JsonObject();

        
        /*
         * FIX 路径参数引起的 CRUD Engine 的解析错误问题
         * 1 / uri = 还原之后的内容，可能会包含 :actor 这种
         * 2 / requestUri = 实际请求的内容，包含具体的参数值，例如 /api/mock/lang
         * 但是这个逻辑会在 CRUD 解析流程中直接反向得到 :actor
         */
        metadata.put(KName.URI, OCacheUri.Tool.recovery(request.path(), request.method()));
        metadata.put(KName.URI_REQUEST, request.path());
        metadata.put(KName.METHOD, request.method().name());
        /*
         * view build for ScRequest to web cache key
         * It's important
         */
        final String literal = request.getParam(KName.VIEW);
        final KView view = KView.create(literal);
        metadata.put(KName.VIEW, view);
        normalized.put(KName.METADATA, metadata);
        /*
         * Build Custom Headers
         */
        final MultiMap inputHeaders = request.headers();
        final JsonObject headers = new JsonObject();
        inputHeaders.forEach(entry -> {
            if (KWeb.HEADER.PARAM_MAP.containsKey(entry.getKey())) {
                headers.put(entry.getKey(), entry.getValue());
            }
        });
        normalized.put("headers", headers);
        /*
         * Build data part ( collect all data )
         */
        return normalized;
    }
}
