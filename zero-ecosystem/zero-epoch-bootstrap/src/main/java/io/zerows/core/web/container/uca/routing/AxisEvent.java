package io.zerows.core.web.container.uca.routing;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.container.handler.CommonEndurer;
import io.zerows.core.web.container.store.under.StoreRouter;
import io.zerows.core.web.container.uca.gateway.SplitterMode;
import io.zerows.core.web.container.uca.gateway.StandardVerifier;
import io.zerows.core.web.io.atom.WrapRequest;
import io.zerows.core.web.io.uca.routing.OAxis;
import io.zerows.core.web.io.uca.routing.OAxisSub;
import io.zerows.core.web.io.zdk.Aim;
import io.zerows.core.web.io.zdk.Sentry;
import io.zerows.core.web.model.atom.Event;
import io.zerows.core.web.model.atom.running.RunRoute;
import io.zerows.core.web.model.atom.running.RunServer;
import io.zerows.core.web.model.store.OCacheActor;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-04
 */
public class AxisEvent implements OAxis {

    private static final Cc<String, SplitterMode> CC_SPLITTER = Cc.openThread();
    private static final Cc<String, Sentry<RoutingContext>> CC_VERIFIER = Cc.openThread();

    private final SplitterMode splitter = CC_SPLITTER.pick(SplitterMode::new);
    private final Sentry<RoutingContext> verifier = CC_VERIFIER.pick(StandardVerifier::new);

    @Override
    public void mount(final RunServer server, final Bundle bundle) {

        final OCacheActor actor = OCacheActor.of(bundle);
        final Set<Event> events = actor.value().getEvents();

        events.stream().filter(Objects::nonNull).forEach(event -> {
            /* 验证 */
            Verifier.verify(event);


            /* 构造 RunRoute 新对象 */
            final RunRoute runRoute = new RunRoute(server).refEvent(event);


            /* 主路由构造处理 **/
            OAxisSub.ofOr(SubAxisUri.class).mount(runRoute, bundle);

            OAxisSub.ofOr(SubAxisMime.class).mount(runRoute, bundle);


            /* 请求封装 */
            final WrapRequest wrapRequest = WrapRequest.create(event);


            /* 绑定执行 */
            final Aim<RoutingContext> aim = this.splitter.distribute(event);


            /* 此处路由处理完成 */
            final Route route = runRoute.instance();
            StoreRouter.of(bundle).addCurrent(runRoute);

            route.handler(this.verifier.signal(wrapRequest))
                .failureHandler(CommonEndurer.create())
                .handler(aim.attack(event))
                .failureHandler(CommonEndurer.create());
        });
    }
}
