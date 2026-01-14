package io.zerows.cosmic.bootstrap;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.zerows.cortex.AxisSub;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.sdk.Axis;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.Filter;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
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

            final Method method = event.getAction();

            try {
                final Filter filter = (Filter) event.getProxy();
                // Init configure;
                filter.init(context);
                // Extract Request/Response
                final HttpServerRequest request = context.request();
                final HttpServerResponse response = context.response();
                filter.doFilter(request, response);

                // Check whether called next or response
                if (!response.ended()) {
                    context.next();
                }
            } catch (final Throwable ex) {
                log.error("[ ZERO ] JSR-340 机制异常", ex);
            }
        });
    }
}
