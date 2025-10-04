package io.zerows.corpus.container;

import io.r2mo.function.Fn;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Annal;
import io.zerows.corpus.exception._40013Exception500ReturnType;
import io.zerows.corpus.exception._40014Exception500WorkerMissing;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.basicore.ActorReceipt;
import io.zerows.epoch.corpus.io.zdk.Aim;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

/**
 * EventBus enabled mode for request
 * 1. AsyncAim: Request -> Agent -> EventBus -> Worker -> Envelop Response
 * 2. OneWayAim: Request -> Agent -> EventBus -> Worker -> (TRUE/FALSE)
 * 5. Vertx AsyncAim: Request -> Agent -> EventBus -> Worker -> void Response(Replier)
 */
class DifferEvent implements Differ<RoutingContext> {

    private static final Annal LOGGER = Annal.get(DifferEvent.class);

    private static final Set<ActorReceipt> RECEIPTS = OCacheActor.entireValue().getReceipts();

    private DifferEvent() {
    }

    public static Differ<RoutingContext> create() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final ActorEvent event) {
        Aim<RoutingContext> aim = null;
        final Method replier = this.findReplier(event);
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || void.class == returnType) {
            // Exception because this method must has return type to
            // send message to event bus. It means that it require
            // return types.
            throw new _40013Exception500ReturnType(method);
        } else {
            final Class<?> replierType = replier.getReturnType();
            if (Void.class == replierType || void.class == replierType) {
                if (this.isAsync(replier)) {
                    // Mode 5: Event Bus: ( Async ) Request-Response
                    aim = Differ.CC_AIMS.pick(AimAsync::new, "Mode Vert.x");
                    // FnZero.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-vert.x", AsyncAim::new);
                } else {
                    // Mode 3: Event Bus: One-Way
                    aim = Differ.CC_AIMS.pick(AimOneWay::new, "Mode OneWay");
                    // aim = FnZero.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-oneway", OneWayAim::new);
                }
            } else {
                // Mode 1: Event Bus: Request-Response
                aim = Differ.CC_AIMS.pick(AimAsync::new, "Mode Java");
                // aim = FnZero.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-java", AsyncAim::new);
            }
        }
        return aim;
    }

    private boolean isAsync(final Method method) {
        boolean async = false;
        final Class<?>[] paramTypes = method.getParameterTypes();
        if (VValue.ONE == paramTypes.length) {
            final Class<?> argumentCls = paramTypes[0];
            if (Message.class == argumentCls) {
                async = true;
            }
        }
        return async;
    }

    @SuppressWarnings("all")
    private Method findReplier(final ActorEvent event) {
        final Annotation annotation = event.getAction().getDeclaredAnnotation(Address.class);
        final String address = Ut.invoke(annotation, "value");
        // Here address mustn't be null or empty
        final ActorReceipt found = RECEIPTS.stream()
            .filter(item -> address.equals(item.getAddress()))
            .findFirst().orElse(null);

        final Method method;
        // Get null found throw exception.
        Fn.jvmKo(Objects.isNull(found), _40014Exception500WorkerMissing.class, address);
        /* Above sentence will throw exception when found is null */
        method = found.getMethod();

        Fn.jvmKo(Objects.isNull(method), _40014Exception500WorkerMissing.class, address);
        return method;
    }

    private static final class InstanceHolder {
        private static final Differ<RoutingContext> INSTANCE = new DifferEvent();
    }
}
