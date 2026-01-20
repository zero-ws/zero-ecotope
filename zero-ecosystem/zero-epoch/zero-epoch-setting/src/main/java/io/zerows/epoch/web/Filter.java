package io.zerows.epoch.web;

import io.vertx.core.Future;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

/**
 * <pre>
 * 🛡️ 增强型 Filter 接口 (JSR-340 风格 + Vert.x 异步增强)
 *
 * 🎯 核心设计目标：
 * 1. **双模支持**：同时兼容传统的同步拦截模式 (Void) 和高性能的异步非阻塞模式 (Future)。
 * 2. **自动降级**：优先执行异步方法 (doAsyncXxx)，若未实现则自动降级执行同步方法 (doXxx)。
 * 3. **职责分离**：开发者只需关注业务逻辑 (放行/拦截)，流程流转 (Next) 由基类 HttpFilter 自动编排。
 *
 * ⚙️ 执行优先级流程 (由 HttpFilter 基类保证)：
 * 1. 框架调用 `doFilter` 入口。
 * 2. 检查是否重写了对应的 `doAsyncXxx` (例如 doAsyncGet)。
 * - ✅ 若重写 (返回非 null Future)：执行异步逻辑 -> 等待 Future 完成 -> 自动 next()。
 * - ❌ 若未重写 (返回 null)：降级执行同步 `doXxx` (例如 doGet) -> 包装为 Future -> 自动 next()。
 * 3. 任何环节调用 `response.end()` 或 `fail()` 都会中断流程，不再执行 next()。
 * </pre>
 *
 * @author lang : 2024-05-04
 */
public interface Filter {

    // HTTP 方法常量定义
    String METHOD_GET = "doGet";
    String METHOD_POST = "doPost";
    String METHOD_PUT = "doPut";
    String METHOD_DELETE = "doDelete";
    String METHOD_OTHER = "doOther";
    String METHOD_FILTER = "doFilter";

    /**
     * 支持的方法集合，用于反射或元数据分析
     */
    Set<String> METHODS = Set.of(METHOD_GET, METHOD_POST, METHOD_PUT, METHOD_DELETE, METHOD_OTHER, METHOD_FILTER);

    /**
     * <pre>
     * 🏁 初始化钩子
     *
     * 在 Filter 实例被挂载到路由时调用。
     * 可用于读取 RoutingContext 中的配置、Session 数据或执行一次性准备工作。
     * </pre>
     *
     * @param context Vert.x 路由上下文
     */
    default void init(final RoutingContext context) {
    }

    /**
     * <pre>
     * 🚦 核心过滤入口 (框架层专用)
     *
     * 这是 Filter 链执行的起点。它负责根据 HTTP Method 分发请求到具体的处理方法。
     *
     * ⚠️ 注意：
     * 通常情况下，子类 **不应该** 覆盖此方法，除非你需要完全接管分发逻辑
     * 或者处理 GET/POST/PUT/DELETE 之外的自定义 HTTP 方法。
     * </pre>
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 异步任务句柄 (Future)，任务完成后框架将自动决定是否放行 (next)
     */
    @SuppressWarnings("all")
    Future<Void> doFilter(HttpServerRequest request, HttpServerResponse response);

    // =========================================================
    // 🌊 异步方法定义 (高优先级 - High Priority)
    // =========================================================

    /**
     * <pre>
     * 🚀 异步处理 GET 请求
     *
     * 适用场景：需要进行数据库查询、Redis 读取、远程 RPC 调用等耗时操作。
     *
     * 行为规范：
     * 1. 若需放行：返回 `Future.succeededFuture()`。
     * 2. 若需拦截：调用 `response.end(...)` 并返回 `Future.succeededFuture()`。
     * 3. 若发生异常：返回 `Future.failedFuture(ex)`。
     *
     * ⚠️ 默认实现返回 null，由基类识别为 "未实现"，并降级调用 `doGet`。
     * </pre>
     *
     * @return 异步结果 Future，或 null (表示降级)
     */
    default Future<Void> doAsyncGet(final HttpServerRequest req, final HttpServerResponse res) {
        return null;
    }

    /**
     * <pre>
     * 🚀 异步处理 POST 请求
     * 行为同 {@link #doAsyncGet}
     * </pre>
     */
    default Future<Void> doAsyncPost(final HttpServerRequest req, final HttpServerResponse res) {
        return null;
    }

    /**
     * <pre>
     * 🚀 异步处理 PUT 请求
     * 行为同 {@link #doAsyncGet}
     * </pre>
     */
    default Future<Void> doAsyncPut(final HttpServerRequest req, final HttpServerResponse res) {
        return null;
    }

    /**
     * <pre>
     * 🚀 异步处理 DELETE 请求
     * 行为同 {@link #doAsyncGet}
     * </pre>
     */
    default Future<Void> doAsyncDelete(final HttpServerRequest req, final HttpServerResponse res) {
        return null;
    }

    // =========================================================
    // 🧱 同步方法定义 (低优先级 - Low Priority)
    // =========================================================

    /**
     * <pre>
     * 🐢 同步处理 GET 请求 (兼容旧有代码)
     *
     * 适用场景：简单的内存逻辑判断 (如 Session 检查、Header 校验)、参数清洗。
     * * 行为规范：
     * 1. 默认行为：什么都不做 (空实现)，基类会自动视为 "通过" 并调用 next()。
     * 2. 若需拦截：调用 `response.end(...)` 或抛出异常。
     * </pre>
     *
     * @throws VertxException 允许抛出 Vert.x 异常，将被框架捕获并处理
     */
    default void doGet(final HttpServerRequest request, final HttpServerResponse response) throws VertxException {
    }

    /**
     * <pre>
     * 🐢 同步处理 POST 请求
     * 行为同 {@link #doGet}
     * </pre>
     */
    default void doPost(final HttpServerRequest request, final HttpServerResponse response) throws VertxException {
    }

    /**
     * <pre>
     * 🐢 同步处理 PUT 请求
     * 行为同 {@link #doGet}
     * </pre>
     */
    default void doPut(final HttpServerRequest request, final HttpServerResponse response) throws VertxException {
    }

    /**
     * <pre>
     * 🐢 同步处理 DELETE 请求
     * 行为同 {@link #doGet}
     * </pre>
     */
    default void doDelete(final HttpServerRequest request, final HttpServerResponse response) throws VertxException {
    }
}