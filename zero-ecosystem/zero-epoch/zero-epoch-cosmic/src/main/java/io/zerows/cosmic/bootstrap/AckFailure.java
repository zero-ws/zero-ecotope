package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.WebException;
import io.r2mo.typed.exception.web._404NotFoundException;
import io.r2mo.typed.exception.web._405MethodBadException;
import io.r2mo.typed.webflow.WebState;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.support.Fx;
import jakarta.ws.rs.core.MediaType;

import java.util.Objects;
import java.util.Set;

/**
 * 故障处理器，用于处理框架级的标准 HTTP 错误响应。
 * <p>
 * 该类主要用于统一生成和返回 404 (Not Found) 和 405 (Method Not Allowed) 等标准 HTTP 错误的响应包。
 * </p>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class AckFailure {
    private static final Cc<String, AckFailure> CCT_FAILURE = Cc.openThread();

    private AckFailure() {
    }

    public static AckFailure of() {
        return CCT_FAILURE.pick(AckFailure::new);
    }

    /**
     * 发送 404 Not Found 响应。
     * <p>
     * 当路由无法匹配任何定义的路径时调用此方法。
     * 该方法会构造标准 Zero 错误格式 (Envelop) 并以 404 状态码返回给客户端。
     * </p>
     * <p>
     * Fix: 解决 404 全局格式问题，调整默认 error 行为。
     * </p>
     *
     * @param context Vert.x Web 路由上下文
     */
    public void reply404(final RoutingContext context) {
        final Envelop error404 = Envelop.failure(new _404NotFoundException("普通异常，无法找到对应资源！"));
        final HttpServerResponse response = context.response();
        response.setStatusCode(404);
        Ack.of(context).handle(error404, response, Set.of(MediaType.APPLICATION_JSON_TYPE));
    }

    /**
     * 发送 405 Method Not Allowed 响应。
     * <p>
     * 当请求的路径存在，但 HTTP 方法（如 GET, POST）不被允许时调用此方法。
     * 该方法会构造标准 Zero 错误格式 (Envelop) 并以 405 状态码返回给客户端。
     * </p>
     * <p>
     * Fix: 解决 405 全局格式问题，调整默认 error 行为。
     * </p>
     *
     * @param context Vert.x Web 路由上下文
     */
    public void reply405(final RoutingContext context) {
        final Envelop error405 = Envelop.failure(new _405MethodBadException("普通异常，方法不被允许！"));
        final HttpServerResponse response = context.response();
        response.setStatusCode(405);
        Ack.of(context).handle(error405, response, Set.of(MediaType.APPLICATION_JSON_TYPE));
    }

    public void reply(final RoutingContext context, final Throwable error) {
        final WebException found = Fx.failAt(error);
        if (Objects.isNull(found)) {
            final Envelop errorEnvelop = Envelop.failure(error);
            final HttpServerResponse response = context.response();
            response.setStatusCode(500);
            Ack.of(context).handle(errorEnvelop, response, Set.of(MediaType.APPLICATION_JSON_TYPE));
            return;
        }

        final HttpServerResponse response = context.response();
        final WebState state = found.getStatus();
        response.setStatusCode(state.state());
        response.setStatusMessage(state.name());
        final Envelop errorEnvelop = Envelop.failure(error);
        Ack.of(context).handle(errorEnvelop, response, Set.of(MediaType.APPLICATION_JSON_TYPE));
    }
}
