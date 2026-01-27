package io.zerows.cosmic.bootstrap;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.zerows.cortex.AxisSub;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.Filter;
import io.zerows.epoch.web.WebEvent;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-04
 */
@Slf4j
public class AxisFilter implements Axis {

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        final OCacheActor actor = OCacheActor.of(bundle);

        final ConcurrentMap<String, Set<WebEvent>> filters = actor.value().getFilters();

        filters.forEach((path, eventSet) -> eventSet.stream().filter(Objects::nonNull).forEach(event -> {
            /* 构造 RunRoute 新对象 */
            final RunRoute runRoute = new RunRoute(server).refEvent(event);


            /* 主路由构造处理 **/
            AxisSub.ofOr(AxisSubUri.class).mount(runRoute, bundle);

            AxisSub.ofOr(AxisSubMime.class).mount(runRoute, bundle);


            /* Filter 处理器 */
            this.mountHandler(runRoute);
        }));
    }

    private void mountHandler(final RunRoute runRoute) {
        final Route route = runRoute.instance();
        route.handler(context -> {
            final WebEvent event = runRoute.refEvent();
            try {
                final Filter filter = (Filter) event.getProxy();
                // Init configure;
                filter.init(context);
                // Extract Request/Response
                final HttpServerRequest request = context.request();
                final HttpServerResponse response = context.response();

                // ✅ 核心修正：全异步调度
                // 1. 移除了同步的 context.next()，防止与 HttpFilter 内部的自动 next 冲突
                // 2. 挂载 onFailure 处理 Filter 内部抛出的异步异常
                filter.doFilter(request, response).onFailure(ex -> {
                    log.error("[ ZERO ] Filter 执行异常", ex);
                    // 兜底保护：如果 Filter 报错且未结束响应，转交给 Vert.x 异常处理
                    if (!response.ended()) {
                        context.fail(ex);
                    }
                });

                // ❌ 已删除旧代码：
                // if (!response.ended()) { context.next(); }
                // 现在的逻辑：是否 next 由 Filter 返回的 Future 完成后自行决定 (HttpFilter 基类负责)

            } catch (final Throwable ex) {
                // 捕获 init 阶段或同步调用产生的 RuntimeException
                log.error("[ ZERO ] JSR-340 机制异常", ex);
                context.fail(ex);
            }
        });
    }
}