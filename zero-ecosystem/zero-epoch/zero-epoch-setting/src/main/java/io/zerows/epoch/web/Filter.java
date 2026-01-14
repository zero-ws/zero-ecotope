package io.zerows.epoch.web;

import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

/**
 * 增强型 Filter 接口
 * 自动根据 HTTP Method 分发到对应的 doXxx 方法
 */
public interface Filter {
    String METHOD_GET = "doGet";
    String METHOD_POST = "doPost";
    String METHOD_PUT = "doPut";
    String METHOD_DELETE = "doDelete";
    String METHOD_OTHER = "doOther";
    String METHOD_FILTER = "doFilter";

    Set<String> METHODS = Set.of(
        METHOD_GET,
        METHOD_POST,
        METHOD_PUT,
        METHOD_DELETE,
        METHOD_OTHER,
        METHOD_FILTER
    );

    /**
     * 初始化方法 (保留原设计)
     */
    default void init(final RoutingContext context) {
    }

    /**
     * 核心分发逻辑
     * <p>
     * 默认实现会根据请求方法 (GET, POST, PUT, DELETE) 自动路由到对应的方法。
     * 如果子类覆盖了此方法，则自动分发失效，由子类全权接管。
     * </p>
     */
    void doFilter(HttpServerRequest request, HttpServerResponse response) throws VertxException;

    // =========================================================
    // 默认方法定义 (子类按需覆盖)
    // =========================================================

    /**
     * 处理 GET 请求
     * 默认行为：放行 (不操作，交由后续 Handler 处理) 或者 405 Method Not Allowed
     * 这里为了兼容 Filter 链模式，默认留空，表示不做特定拦截
     */
    default void doGet(final HttpServerRequest request, final HttpServerResponse response)
        throws VertxException {
    }

    /**
     * 处理 POST 请求
     */
    default void doPost(final HttpServerRequest request, final HttpServerResponse response)
        throws VertxException {
    }

    /**
     * 处理 PUT 请求
     */
    default void doPut(final HttpServerRequest request, final HttpServerResponse response)
        throws VertxException {
    }

    /**
     * 处理 DELETE 请求
     */
    default void doDelete(final HttpServerRequest request, final HttpServerResponse response)
        throws VertxException {
    }
}