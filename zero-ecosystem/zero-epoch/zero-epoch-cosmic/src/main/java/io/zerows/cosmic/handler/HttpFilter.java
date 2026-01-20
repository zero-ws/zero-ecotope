package io.zerows.cosmic.handler;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.zerows.cortex.exception._40051Exception500FilterContext;
import io.zerows.cosmic.bootstrap.AckFailure;
import io.zerows.epoch.web.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 * 🛡️ 核心组件：HTTP 过滤器基类 (HttpFilter)
 *
 * 🎯 核心职责：
 * 本类作为所有 HTTP 过滤器的父类，实现了 <b>模板方法模式 (Template Method)</b>。
 * 它接管了复杂的流程控制，让开发者只需关注业务逻辑（放行还是拦截）。
 *
 * ⚙️ 核心机制：
 * 1. <b>智能调度 (Smart Dispatch)</b>：
 * - 优先检查子类是否实现了异步方法 ({@code doAsyncXxx})。
 * - 若未实现 (返回 null)，自动降级调用同步方法 ({@code doXxx}) 并将其包装为 Future。
 *
 * 2. <b>自动编排 (Auto Orchestration)</b>：
 * - 开发者无需手动调用 {@code context.next()}。
 * - 当业务逻辑执行成功 (Future completed) 且响应未结束时，本类会自动触发 {@code next()}。
 *
 * 3. <b>双重保险 (Safety Guard)</b>：
 * - 内置防重入锁 ({@code autoNextTriggered})，防止因并发或逻辑错误导致多次调用 {@code next()}。
 * - 自动识别响应状态，若业务代码调用了 {@code response.end()}，自动停止流转。
 * </pre>
 */
public abstract class HttpFilter implements Filter {

    /**
     * 内部流转标记，用于防止父类逻辑或子类手动调用导致重复触发 next()。
     * 这解决了 Vert.x 中常见的 "Double Next" 问题。
     */
    private boolean autoNextTriggered = false;

    /**
     * 当前请求的路由上下文
     */
    private RoutingContext context;

    /**
     * <pre>
     * 🏁 初始化上下文
     *
     * 注入 Vert.x 的 RoutingContext，供后续流程使用。
     * 同时调用无参的 {@link #init()} 供子类扩展。
     * </pre>
     *
     * @param context Vert.x 路由上下文
     */
    @Override
    public void init(final RoutingContext context) {
        this.context = context;
        this.init();
    }

    /**
     * <pre>
     * 🚦 核心分发与编排逻辑 (Template Method)
     *
     * 这是框架调用的主入口。它不包含具体业务逻辑，而是负责：
     * 1. <b>Method 分发</b>：根据 HTTP Method 找到对应的处理方法。
     * 2. <b>策略选择</b>：决定是直接执行异步任务，还是执行同步任务并 Bridge 成异步任务。
     * 3. <b>结果处理</b>：
     * - {@code onSuccess}: 进入 {@link #tryAutoNext} 尝试放行。
     * - {@code onFailure}: 转交 {@link AckFailure} 进行统一异常响应。
     * </pre>
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @return 统一的异步任务句柄
     */
    @Override
    public Future<Void> doFilter(final HttpServerRequest request,
                                 final HttpServerResponse response) {
        final HttpMethod method = request.method();

        Future<Void> task;

        try {
            // 1. 尝试调度：优先 Async，降级 Sync
            if (HttpMethod.GET == method) {
                task = this.dispatch(this.doAsyncGet(request, response), () -> this.doGet(request, response));
            } else if (HttpMethod.POST == method) {
                task = this.dispatch(this.doAsyncPost(request, response), () -> this.doPost(request, response));
            } else if (HttpMethod.PUT == method) {
                task = this.dispatch(this.doAsyncPut(request, response), () -> this.doPut(request, response));
            } else if (HttpMethod.DELETE == method) {
                task = this.dispatch(this.doAsyncDelete(request, response), () -> this.doDelete(request, response));
            } else {
                // 其他方法 (如 OPTIONS, HEAD) 默认视为成功，进入自动放行流程
                task = Future.succeededFuture();
            }
        } catch (final Throwable ex) {
            // 捕获分发过程中的同步异常（如 dispatch 内部错误）
            task = Future.failedFuture(ex);
        }

        // 2. 自动编排 (Auto Orchestration)
        return task
            .onSuccess(v -> this.tryAutoNext(response)) // 成功：尝试自动下一级
            .onFailure(ex -> AckFailure.of().reply(this.context, ex)); // 失败：交给异常处理
    }

    /**
     * <pre>
     * ⚖️ 调度器：异步优先策略
     *
     * 判断子类是否重写了异步方法 (返回非 null)。
     * - 是：直接使用子类的 Future。
     * - 否：将同步方法的 {@link Runnable} 包装成 {@link Future} 执行。
     * </pre>
     *
     * @param asyncResult 子类异步方法的返回值 (可能为 null)
     * @param syncRunner  对应的同步方法封装
     * @return 统一的 Future 对象
     */
    private Future<Void> dispatch(final Future<Void> asyncResult, final Runnable syncRunner) {
        // 如果子类重写了 doAsyncXxx (返回非null)，直接使用
        if (asyncResult != null) {
            return asyncResult;
        }

        // 否则执行同步方法，并将其“异步化”
        // 这样可以捕获同步代码块中的 RuntimeException 并通过 Future 传递
        return Future.future(promise -> {
            try {
                syncRunner.run();
                promise.complete();
            } catch (final Throwable e) {
                promise.fail(e);
            }
        });
    }

    /**
     * <pre>
     * 🤖 自动编排核心 (Auto Pilot)
     *
     * 决定是否调用 {@code context.next()}。
     * 只有同时满足以下条件才会放行：
     * 1. 之前没有触发过 next (防重入)。
     * 2. 响应对象没有结束 (未调用 end/close)。
     *
     * 💡 开发者提示：
     * 如果你在业务逻辑中调用了 {@code response.end()}，此方法会自动感知并停止链条流转。
     * </pre>
     *
     * @param response HTTP 响应对象
     */
    private void tryAutoNext(final HttpServerResponse response) {
        // 双重保险：
        // 1. 开发者如果已经在代码里手动调了 next (虽然不建议)，这里就不调了
        // 2. 这里的标记是防止本方法被多次调用 (例如 Future 重复回调)
        if (this.autoNextTriggered) {
            return;
        }

        // 核心判断：开发者是否拦截了请求？
        // 拦截标志 = response.end() / response.close()
        // 此时响应已发送给客户端，不应继续执行后续 Handler
        if (response.ended() || response.closed()) {
            return;
        }

        // 标记并放行
        this.autoNextTriggered = true;
        this.context.next();
    }

    // =========================================================================
    // 🛠️ 辅助工具
    // =========================================================================

    /**
     * <pre>
     * ⏭️ 手动放行 (Escape Hatch)
     *
     * ⚠️ 通常情况下，开发者不需要也不应该调用此方法。基类会自动处理。
     * 仅在极其特殊的复杂异步场景下，需要提前手动放行时使用。
     * 调用此方法会更新 {@code autoNextTriggered} 标记，阻止基类后续的自动放行。
     * </pre>
     */
    protected void next() {
        if (!this.autoNextTriggered && !this.context.response().ended()) {
            this.autoNextTriggered = true;
            this.context.next();
        }
    }

    /**
     * 向路由上下文中写入数据
     *
     * @param key   键
     * @param value 值
     */
    protected void put(final String key, final Object value) {
        this.context.put(key, value);
    }

    /**
     * 从路由上下文中读取数据 (自动转型)
     *
     * @param key 键
     * @param <T> 目标类型
     * @return 值，若无则返回 null
     */
    @SuppressWarnings("unchecked")
    protected <T> T get(final String key) {
        final Object reference = this.context.get(key);
        return null == reference ? null : (T) reference;
    }

    /**
     * 获取当前 Session
     */
    protected Session getSession() {
        return this.context.session();
    }

    /**
     * 获取原始 RoutingContext
     */
    protected RoutingContext getContext() {
        return this.context;
    }

    /**
     * 获取所有 Cookie (Map 形式)
     */
    protected Map<String, Cookie> getCookies() {
        return this.context.request()
            .cookies()
            .stream()
            .collect(Collectors.toMap(Cookie::getName, cookie -> cookie));
    }

    /**
     * 获取当前类的 Logger 实例
     */
    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * 子类初始化钩子，用于校验上下文是否注入成功
     */
    public void init() {
        Fn.jvmKo(Objects.isNull(this.context), _40051Exception500FilterContext.class);
    }
}