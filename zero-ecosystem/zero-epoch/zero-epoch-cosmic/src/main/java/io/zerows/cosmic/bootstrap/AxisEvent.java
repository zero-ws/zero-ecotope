package io.zerows.cosmic.bootstrap;

import io.r2mo.typed.cc.Cc;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.AxisSub;
import io.zerows.cortex.management.StoreRouter;
import io.zerows.cortex.metadata.RunRoute;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.sdk.Aim;
import io.zerows.cortex.sdk.Axis;
import io.zerows.cosmic.handler.EndurerCommon;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.specification.development.compiled.HBundle;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-04
 */
@Slf4j
public class AxisEvent implements Axis {

    private static final Cc<String, SentryMode> CC_SPLITTER = Cc.openThread();
    private static final Cc<String, Sentry<RoutingContext>> CC_VERIFIER = Cc.openThread();

    private final SentryMode splitter = CC_SPLITTER.pick(SentryMode::new);
    private final Sentry<RoutingContext> verifier = CC_VERIFIER.pick(SentryVerifier::new);

    @Override
    public void mount(final RunServer server, final HBundle bundle) {
        final OCacheActor actor = OCacheActor.of(bundle);
        final Set<WebEvent> events = actor.value().getEvents();
        events.stream().filter(Objects::nonNull).forEach(event -> {
            /* 验证 */
            AxisVerifier.verify(event);


            /* 构造 RunRoute 新对象 */
            final RunRoute runRoute = new RunRoute(server).refEvent(event);


            /* 主路由构造处理 **/
            AxisSub.ofOr(AxisSubUri.class).mount(runRoute, bundle);

            AxisSub.ofOr(AxisSubMime.class).mount(runRoute, bundle);


            /* 请求封装 */
            final WebRequest wrapRequest = WebRequest.create(event);


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
