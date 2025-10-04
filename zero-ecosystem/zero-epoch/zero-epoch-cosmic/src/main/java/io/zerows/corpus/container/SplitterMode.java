package io.zerows.corpus.container;

import io.r2mo.function.Fn;
import io.vertx.ext.web.RoutingContext;
import io.zerows.corpus.exception._40042Exception500ChannelMulti;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Ipc;
import io.zerows.epoch.basicore.ActorEvent;
import io.zerows.epoch.corpus.io.zdk.Aim;

import java.lang.reflect.Method;

/**
 * Splitter to getNull executor reference.
 * It will happen in startup of route building to avoid
 * request resource spending.
 * 1. Level 1: Distinguish whether enable EventBus
 * EventBus mode: Async
 * Non-EventBus mode: Sync
 * 2. Level 2: Distinguish the request mode
 * One-Way mode: No response needed. ( Return Type )
 * Request-Response mode: Must require response. ( Return Type )
 * Support modes:
 * 1. AsyncAim: Event Bus: Request-Response
 * 2. SyncAim: Non-Event Bus: Request-Response
 * 3. OneWayAim: Event Bus: One-Way
 * 4. BlockAim: Non-Event Bus: One-Way
 * 5. Vert.x Style Request -> Event -> Response
 * 6. Rpc Style for @Ipc annotation
 */
public class SplitterMode {

    public Aim<RoutingContext> distribute(final ActorEvent event) {
        // 1. Scan method to check @Address
        final Method method = event.getAction();
        final boolean annotated = method.isAnnotationPresent(Address.class);
        final boolean rpc = method.isAnnotationPresent(Ipc.class);
        // 2. Only one channel enabled
        Fn.jvmKo(rpc && annotated, _40042Exception500ChannelMulti.class, method);

        final Differ<RoutingContext> differ;
        if (annotated) {
            // EventBus Mode for Mode: 1,3,5
            differ = DifferEvent.create();
        } else if (rpc) {

            // Ipc Mode for Mode: 6
            differ = DifferIpc.create();
        } else {

            // Non Event Bus for Mode: 2,4
            differ = DifferCommon.create();
        }
        return differ.build(event);
    }
}
