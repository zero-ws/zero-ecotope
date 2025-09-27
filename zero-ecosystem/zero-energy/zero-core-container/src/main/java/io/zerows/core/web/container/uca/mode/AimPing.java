package io.zerows.core.web.container.uca.mode;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.util.Ut;
import io.zerows.core.web.io.zdk.Aim;
import io.zerows.core.web.model.atom.Event;
import io.zerows.core.web.model.commune.Envelop;

/**
 * BlockAim: Non-Event Bus: One-Way
 */
public class AimPing extends AbstractAim implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final Event event) {
        return (context) -> this.exec(() -> {
            // 1. Build TypedArgument
            final Object[] arguments = this.buildArgs(context, event);

            // 2. Method call
            final Object invoked = this.invoke(event, arguments);
            // 3. Resource model building
            final Envelop data;
            if (Ut.isBoolean(invoked)) {
                data = Envelop.success(invoked);
            } else {
                data = Envelop.success(Boolean.TRUE);
            }
            // 4. Process modal
            Answer.reply(context, data, event);
        }, context, event);
    }
}
