package io.zerows.cosmic.bootstrap;

import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.web.WebEvent;
import io.zerows.support.Ut;

import java.lang.reflect.Method;

/**
 * EventBus disabled for request
 * 1. SyncAim: Non-Event Bus: Request -> Response
 * 2. BlockAim: Non-Event Bus: Request -> (TRUE/FALSE)
 */
class DifferCommon implements Differ<RoutingContext> {

    private DifferCommon() {
    }

    public static Differ<RoutingContext> create() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final WebEvent event) {
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        Aim<RoutingContext> aim = null;
        if (Void.class == returnType || void.class == returnType) {
            // Mode 4: Non-Event Bus: One-Way
            aim = Differ.CC_AIMS.pick(() -> Ut.instance(AimSPing.class), AimType.SYNC_PING.name());
        } else {
            // Mode 2: Non-Event Bus: Request-Response\
            aim = Differ.CC_AIMS.pick(() -> Ut.instance(AimSReply.class), AimType.SYNC_REPLY.name());
        }
        return aim;
    }

    private static final class InstanceHolder {
        private static final Differ<RoutingContext> INSTANCE = new DifferCommon();
    }
}
