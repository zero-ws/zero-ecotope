package io.zerows.epoch.corpus.container.uca.gateway;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.container.uca.mode.AimPing;
import io.zerows.epoch.corpus.container.uca.mode.AimSync;
import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.epoch.corpus.model.Event;
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
    public Aim<RoutingContext> build(final Event event) {
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        Aim<RoutingContext> aim = null;
        if (Void.class == returnType || void.class == returnType) {
            // Mode 4: Non-Event Bus: One-Way
            aim = CACHE.CC_AIMS.pick(() -> Ut.instance(AimPing.class), "Mode Ping");
            // FnZero.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-ping", () -> Ut.instance(PingAim.class));
        } else {
            // Mode 2: Non-Event Bus: Request-Response\
            aim = CACHE.CC_AIMS.pick(() -> Ut.instance(AimSync.class), "Mode Sync");
            // FnZero.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-sync", () -> Ut.instance(SyncAim.class));
        }
        return aim;
    }

    private static final class InstanceHolder {
        private static final Differ<RoutingContext> INSTANCE = new DifferCommon();
    }
}
