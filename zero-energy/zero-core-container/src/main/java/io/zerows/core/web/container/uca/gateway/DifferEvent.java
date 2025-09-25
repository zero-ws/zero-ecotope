package io.zerows.core.web.container.uca.gateway;

import io.zerows.ams.constant.VValue;
import io.zerows.core.fn.Fx;
import io.zerows.core.uca.log.Annal;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.annotations.Address;
import io.zerows.core.util.Ut;
import io.zerows.core.web.container.exception.BootReturnTypeException;
import io.zerows.core.web.container.exception.BootWorkerMissingException;
import io.zerows.core.web.container.uca.mode.AimAsync;
import io.zerows.core.web.container.uca.mode.AimOneWay;
import io.zerows.core.web.io.zdk.Aim;
import io.zerows.core.web.model.atom.Event;
import io.zerows.core.web.model.atom.Receipt;
import io.zerows.core.web.model.store.OCacheActor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * EventBus enabled mode for request
 * 1. AsyncAim: Request -> Agent -> EventBus -> Worker -> Envelop Response
 * 2. OneWayAim: Request -> Agent -> EventBus -> Worker -> (TRUE/FALSE)
 * 5. Vertx AsyncAim: Request -> Agent -> EventBus -> Worker -> void Response(Replier)
 */
class DifferEvent implements Differ<RoutingContext> {

    private static final Annal LOGGER = Annal.get(DifferEvent.class);

    private static final Set<Receipt> RECEIPTS = OCacheActor.entireValue().getReceipts();

    private DifferEvent() {
    }

    public static Differ<RoutingContext> create() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final Event event) {
        Aim<RoutingContext> aim = null;
        final Method replier = this.findReplier(event);
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || void.class == returnType) {
            // Exception because this method must has return type to
            // send message to event bus. It means that it require
            // return types.
            Fx.outBoot(true, LOGGER, BootReturnTypeException.class,
                this.getClass(), method);
        } else {
            final Class<?> replierType = replier.getReturnType();
            if (Void.class == replierType || void.class == replierType) {
                if (this.isAsync(replier)) {
                    // Mode 5: Event Bus: ( Async ) Request-Response
                    aim = CACHE.CC_AIMS.pick(AimAsync::new, "Mode Vert.x");
                    // Fx.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-vert.x", AsyncAim::new);
                } else {
                    // Mode 3: Event Bus: One-Way
                    aim = CACHE.CC_AIMS.pick(AimOneWay::new, "Mode OneWay");
                    // aim = Fx.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-oneway", OneWayAim::new);
                }
            } else {
                // Mode 1: Event Bus: Request-Response
                aim = CACHE.CC_AIMS.pick(AimAsync::new, "Mode Java");
                // aim = Fx.po?l(Pool.AIMS, Thread.currentThread().getName() + "-mode-java", AsyncAim::new);
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
    private Method findReplier(final Event event) {
        final Annotation annotation = event.getAction().getDeclaredAnnotation(Address.class);
        final String address = Ut.invoke(annotation, "value");
        // Here address mustn't be null or empty
        final Receipt found = RECEIPTS.stream()
            .filter(item -> address.equals(item.getAddress()))
            .findFirst().orElse(null);

        final Method method;
        // Get null found throw exception.
        Fx.outBoot(null == found, LOGGER, BootWorkerMissingException.class,
            this.getClass(), address);
        /* Above sentence will throw exception when found is null */
        method = found.getMethod();

        Fx.outBoot(null == method, LOGGER, BootWorkerMissingException.class,
            this.getClass(), address);
        return method;
    }

    private static final class InstanceHolder {
        private static final Differ<RoutingContext> INSTANCE = new DifferEvent();
    }
}
