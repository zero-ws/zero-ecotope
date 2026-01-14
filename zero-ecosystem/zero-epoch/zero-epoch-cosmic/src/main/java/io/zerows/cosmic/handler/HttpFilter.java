package io.zerows.cosmic.handler;

import io.r2mo.function.Fn;
import io.vertx.core.VertxException;
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
 * 🛡️ 核心组件：HTTP 过滤器基类
 *
 * 🎯 作用：
 * 该类是所有自定义 HTTP 过滤器的父类，提供了丰富的基础功能。
 * 它实现了 `Filter` 接口，并负责与 Vert.x 的 `RoutingContext` 进行交互。
 *
 * ⚡️ 核心功能：
 * 1. 上下文管理：自动注入和管理 `RoutingContext`。
 * 2. 数据传递：提供 `put/get` 方法在 Filter 链和 Agent 之间传递数据。
 * 3. 流程控制：实现了标准的 `doFilter` 模板方法，确立了 "执行 -> 异常处理 -> 放行" 的标准流程。
 * 4. 辅助工具：提供 Session、Cookie、Logger 等常用对象的快捷访问。
 *
 * ⚙️ 执行流程：
 * init() -> doFilter() [doGet/doPost...] -> doFilterContinue() -> Next Filter/Handler
 * </pre>
 */
public abstract class HttpFilter implements Filter {
    private boolean isNexted = false;
    private RoutingContext context;

    /**
     * <pre>
     * 🏁 初始化过滤器上下文
     *
     * 行为：
     * 接收 Vert.x 的 `RoutingContext` 并保存，用于后续操作。
     * 同时调用无参的 `init()` 供子类进行自定义初始化。
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
     * 💾 写入上下文数据
     *
     * 作用：
     * 将键值对数据存储到 `RoutingContext` 中。
     * 这些数据可以在后续的 Filter 或 Handler (Agent) 中被读取。
     * </pre>
     *
     * @param key   数据的键名
     * @param value 数据的值
     */
    protected void put(final String key, final Object value) {
        this.context.put(key, value);
    }

    /**
     * <pre>
     * 📖 读取上下文数据
     *
     * 作用：
     * 从 `RoutingContext` 中获取指定键名的数据。
     * 支持泛型自动转型。
     * </pre>
     *
     * @param key 数据的键名
     * @param <T> 数据类型
     * @return 获取到的数据，若不存在则返回 null
     */
    @SuppressWarnings("unchecked")
    protected <T> T get(final String key) {
        final Object reference = this.context.get(key);
        return null == reference ? null : (T) reference;
    }

    /**
     * <pre>
     * 🚦 核心过滤逻辑执行器
     *
     * 行为：
     * 1. 根据 HTTP Method 分发请求到 `doGet`, `doPost` 等方法。
     * 2. 捕获执行过程中的所有异常，并转交给 `AckFailure` 进行统一处理。
     * 3. 无论执行是否成功（除非响应已结束），都会尝试调用 `doFilterContinue` 继续执行链条。
     *
     * ⚠️ 注意：
     * 这是模板方法，通常不需要子类覆盖，除非需要改变核心分发流程。
     * </pre>
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws VertxException Vert.x 异常
     */
    @Override
    public void doFilter(final HttpServerRequest request,
                         final HttpServerResponse response) throws VertxException {
        final HttpMethod method = request.method();

        try {
            if (HttpMethod.GET == method) {
                this.doGet(request, response);
            } else if (HttpMethod.POST == method) {
                this.doPost(request, response);
            } else if (HttpMethod.PUT == method) {
                this.doPut(request, response);
            } else if (HttpMethod.DELETE == method) {
                this.doDelete(request, response);
            }
            this.doFilterContinue(request, response);
        } catch (final Throwable ex) {
            // 直接抛出异常，转交 Handler
            AckFailure.of().reply(this.context, ex);
        }
    }

    /**
     * <pre>
     * ⏭️ 过滤器链流转控制
     *
     * 行为：
     * 判断是否需要将请求传递给下一个处理器。
     * 如果响应已经关闭（ended），或已经流转过（isNexted），则停止流转。
     * 否则，调用 `context.next()` 驱动 Vert.x 路由链继续执行。
     * </pre>
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     */
    private void doFilterContinue(final HttpServerRequest request,
                                  final HttpServerResponse response) {
        // If response end it means that it's not needed to move next.
        if (this.isNexted) {
            return;
        }
        if (response.ended()) {
            return;
        }

        // 标记放行
        this.isNexted = true;
        this.context.next();
    }

    /**
     * <pre>
     * 📦 获取 Session 对象
     *
     * @return 当前请求关联的 Session
     * </pre>
     */
    protected Session getSession() {
        return this.context.session();
    }

    /**
     * <pre>
     * 🧩 获取路由上下文
     *
     * @return 原始的 Vert.x RoutingContext 对象
     * </pre>
     */
    protected RoutingContext getContext() {
        return this.context;
    }

    /**
     * <pre>
     * 🍪 获取 Cookies 集合
     *
     * 行为：
     * 将请求中的 Cookie 列表转换为 Map 结构，方便按名称查找。
     *
     * @return Cookie 名称到 Cookie 对象的映射表
     * </pre>
     */
    protected Map<String, Cookie> getCookies() {
        return this.context.request()
            .cookies()
            .stream()
            .collect(Collectors.toMap(Cookie::getName, cookie -> cookie));
    }

    /**
     * <pre>
     * 📝 获取日志记录器
     *
     * 行为：
     * 根据当前类的实际类型获取 SLF4J Logger 实例。
     *
     * @return Logger 实例
     * </pre>
     */
    protected Logger log() {
        return LoggerFactory.getLogger(this.getClass());
    }

    /**
     * <pre>
     * ⚙️ 自定义初始化钩子
     *
     * 作用：
     * 供子类覆盖，用于执行特定的初始化逻辑。
     * 在 `init(RoutingContext)` 中被自动调用。
     * 默认实现会检查 context 是否为空，确保初始化流程正确。
     * </pre>
     */
    public void init() {
        Fn.jvmKo(Objects.isNull(this.context), _40051Exception500FilterContext.class);
    }
}
