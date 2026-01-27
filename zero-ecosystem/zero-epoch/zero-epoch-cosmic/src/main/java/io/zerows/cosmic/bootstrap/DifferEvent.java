package io.zerows.cosmic.bootstrap;

import io.r2mo.function.Fn;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.cosmic.exception._40013Exception500ReturnType;
import io.zerows.cosmic.exception._40014Exception500WorkerMissing;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.management.OCacheActor;
import io.zerows.epoch.web.WebEvent;
import io.zerows.epoch.web.WebReceipt;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * EventBus enabled mode for request
 * 1. AsyncAim: Request -> Agent -> EventBus -> Worker -> Envelop Response
 * 2. OneWayAim: Request -> Agent -> EventBus -> Worker -> (TRUE/FALSE)
 * 5. Vertx AsyncAim: Request -> Agent -> EventBus -> Worker -> void Response(Replier)
 */
class DifferEvent implements Differ<RoutingContext> {

    private static final Set<WebReceipt> RECEIPTS = OCacheActor.entireValue().getReceipts();

    private DifferEvent() {
    }

    public static Differ<RoutingContext> create() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Aim<RoutingContext> build(final WebEvent event) {
        final Aim<RoutingContext> aim;
        final Method replier = this.findReplier(event);
        final Method method = event.getAction();
        final Class<?> returnType = method.getReturnType();
        if (Void.class == returnType || void.class == returnType) {
            // Exception because this method must has return type to
            // send message to event bus. It means that it require
            // return types.
            throw new _40013Exception500ReturnType(method);
        }


        final Class<?> replierType = replier.getReturnType();
        if (Void.class != replierType && void.class != replierType) {
            // Mode 1: Event Bus: Request-Response
            aim = Differ.CC_AIMS.pick(AimAStandard::new, AimType.ASYNC_STANDARD.name());
            return aim;     // 截断
        }

        if (this.isAsync(replier)) {
            // Mode 5: Event Bus: Callback
            aim = Differ.CC_AIMS.pick(AimAStandard::new, AimType.ASYNC_CALLBACK.name());
        } else {
            // Mode 3: Event Bus: One-Way
            aim = Differ.CC_AIMS.pick(AimAOneWay::new, AimType.ASYNC_ONEWAY.name());
        }
        return aim;
    }

    /**
     * 拓展异步检查，不检查第一个参数，只要有一个参数是 {@link Message} 接口类型即视为异步回调
     * <pre>
     *     1. 条件1：返回值必须是 void.class / Void.class
     *     2. 条件2：参数列表中必须包含且仅包含一个参数，且该参数类型是 {@link Message} 接口类型
     * </pre>
     *
     * @param method Method reference
     * @return 是否异步
     */
    private boolean isAsync(final Method method) {
        return Arrays.stream(method.getParameterTypes())
            .anyMatch(Message.class::isAssignableFrom);
    }

    @SuppressWarnings("all")
    private Method findReplier(final WebEvent event) {
        final Address annotation = event.getAction().getDeclaredAnnotation(Address.class);
        final String address = annotation.value();
        // Here address mustn't be null or empty
        final WebReceipt found = RECEIPTS.stream()
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
