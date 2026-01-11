package io.zerows.cosmic.bootstrap;

import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.cosmic.exception._40013Exception500ReturnType;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.support.Ut;

import java.lang.reflect.Method;

class DifferIpc implements Differ<RoutingContext> {

    private DifferIpc() {
    }

    public static Differ<RoutingContext> create() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final WebEvent event) {
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        // Rpc Mode only
        final Aim<RoutingContext> aim;
        if (Void.class == returnType || void.class == returnType) {
            throw new _40013Exception500ReturnType(method);
        }

        // Mode 6: Ipc channel enabled
        aim = Differ.CC_AIMS.pick(() -> Ut.instance(AimIpc.class), AimType.IPC_COMMON.name());
        return aim;
    }

    private static final class InstanceHolder {
        private static final Differ<RoutingContext> INSTANCE = new DifferIpc();
    }
}
