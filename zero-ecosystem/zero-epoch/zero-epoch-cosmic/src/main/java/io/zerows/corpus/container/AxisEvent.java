package io.zerows.corpus.container;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.zerows.corpus.handler.EndurerCommon;
import io.zerows.corpus.management.StoreRouter;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.io.atom.WrapRequest;
import io.zerows.epoch.corpus.io.uca.routing.OAxis;
import io.zerows.epoch.corpus.io.uca.routing.OAxisSub;
import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.epoch.corpus.io.zdk.Sentry;
import io.zerows.epoch.corpus.model.running.RunRoute;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-04
 */
public class AxisEvent implements OAxis {

    private static final Cc<String, SplitterMode> CC_SPLITTER = Cc.openThread();
    private static final Cc<String, Sentry<RoutingContext>> CC_VERIFIER = Cc.openThread();

    private final SplitterMode splitter = CC_SPLITTER.pick(SplitterMode::new);
    private final Sentry<RoutingContext> verifier = CC_VERIFIER.pick(SplitterVerifier::new);

    @Override
    public void mount(final RunServer server, final HBundle bundle) {

        final OCacheActor actor = OCacheActor.of(bundle);
        final Set<ActorEvent> events = actor.value().getEvents();

        events.stream().filter(Objects::nonNull).forEach(event -> {
            /* 验证 */
            AxisVerifier.verify(event);


            /* 构造 RunRoute 新对象 */
            final RunRoute runRoute = new RunRoute(server).refEvent(event);


            /* 主路由构造处理 **/
            OAxisSub.ofOr(AxisSubUri.class).mount(runRoute, bundle);

            OAxisSub.ofOr(AxisSubMime.class).mount(runRoute, bundle);


            /* 请求封装 */
            final WrapRequest wrapRequest = WrapRequest.create(event);


            /* 绑定执行 */
            final Aim<RoutingContext> aim = this.splitter.distribute(event);


            /* 此处路由处理完成 */
            final Route route = runRoute.instance();
            StoreRouter.of(bundle).addCurrent(runRoute);

            route.handler(this.verifier.signal(wrapRequest))
                .failureHandler(EndurerCommon.create())
                .handler(aim.attack(event))
                .failureHandler(EndurerCommon.create());
        });
    }
}
