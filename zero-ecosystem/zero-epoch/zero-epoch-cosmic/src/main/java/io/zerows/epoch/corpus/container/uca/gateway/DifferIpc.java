package io.zerows.epoch.corpus.container.uca.gateway;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.common.log.Annal;
import io.zerows.epoch.corpus.container.exception._40013Exception500ReturnType;
import io.zerows.epoch.corpus.container.uca.mode.AimIpc;
import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.epoch.corpus.model.Event;
import io.zerows.epoch.program.Ut;

import java.lang.reflect.Method;

class DifferIpc implements Differ<RoutingContext> {

    private static final Annal LOGGER = Annal.get(DifferIpc.class);

    private DifferIpc() {
    }

    public static Differ<RoutingContext> create() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final Event event) {
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        // Rpc Mode only
        Aim<RoutingContext> aim = null;
        if (Void.class == returnType || void.class == returnType) {
            // Exception because this method must has return type to
            // send message to event bus. It means that it require
            // return types.
            throw new _40013Exception500ReturnType(method);
        } else {
            // Mode 6: Ipc channel enabled
            aim = CACHE.CC_AIMS.pick(() -> Ut.instance(AimIpc.class), "Mode Ipc");
            // FnZero.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-ipc",() -> Ut.instance(IpcAim.class));
        }
        return aim;
    }

    private static final class InstanceHolder {
        private static final Differ<RoutingContext> INSTANCE = new DifferIpc();
    }
}
