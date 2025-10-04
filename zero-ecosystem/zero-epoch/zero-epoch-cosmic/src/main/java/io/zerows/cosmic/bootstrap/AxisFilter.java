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
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-04
 */
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
            final Object proxy = event.getProxy();

            try {
                // Init context;
                Ut.invoke(proxy, "init", context);
                // Extract Request/Response
                final HttpServerRequest request = context.request();
                final HttpServerResponse response = context.response();
                method.invoke(proxy, request, response);

                // Check whether called next or response
                if (!response.ended()) {
                    context.next();
                }
            } catch (final Throwable ex) {
                Ut.Log.invoke(this.getClass()).fatal(ex);
            }
        });
    }
}
