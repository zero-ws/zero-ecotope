package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.web.Envelop;

/**
 * OneWayAim: Event Bus: One-Way
 */
public class AimOneWay extends AimBase implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Build TypedArgument by java reflection metadata definition here
             */
            final Object[] arguments = this.buildArgs(context, event);

            /*
             * Method callxx
             * Java reflector to call developer's defined method
             */
            final Object returnValue = this.invoke(event, arguments);

            /*
             * Build event bus
             * This aim is async mode, it should enable Event Bus in new version instead of
             * bus.send here.
             */
            final Vertx vertx = context.vertx();
            final EventBus bus = vertx.eventBus();
            final String address = this.address(event);

            /*
             * Call Flower next method to findRunning future
             * This future is async and we must set handler to capture the result of this future
             * here.
             */
            final Future<Envelop> future = AckThen.next(context, returnValue);

            /*
             * Event bus send request out instead of other method
             * Please refer following old code to compare.
             */
            future.onComplete(dataRes -> {
                /*
                 * To avoid null dot result when the handler triggered result here
                 * SUCCESS
                 */
                if (dataRes.succeeded()) {
                    final DeliveryOptions deliveryOptions = NodeStore.ofDelivery(vertx);
                    bus.<Envelop>request(address, dataRes.result(), deliveryOptions).onComplete(handler -> {
                        final Envelop response;
                        if (handler.succeeded()) {
                            /*
                             * // One Way message
                             * Only TRUE returned.
                             */
                            response = Envelop.success(Boolean.TRUE);
                        } else {
                            response = this.failure(address, handler);
                        }
                        AckFlow.reply(context, response, event);
                    });
                }
            });
        }, context, event);
    }
}
