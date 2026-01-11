package io.zerows.cosmic.bootstrap;

import io.r2mo.spi.SPI;
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
public class AimAOneWay extends AimAsync implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Method callxx
             * Java reflector to call developer's defined method
             */
            final Future<Envelop> future = this.invokeAsync(context, event);

            /*
             * Build event bus
             * This aim is async mode, it should enable Event Bus in new version instead of
             * bus.send here.
             */
            final Vertx vertx = context.vertx();
            final EventBus bus = vertx.eventBus();
            final String address = this.address(event);

            /*
             * Event bus send request out instead of other method
             * Please refer following old code to compare.
             */
            future.onComplete(dataRes -> {
                /*
                 * To avoid null dot result when the handler triggered result here
                 * SUCCESS
                 */
                if (dataRes.failed()) {
                    return;
                }

                final DeliveryOptions deliveryOptions = NodeStore.ofDelivery(vertx);
                bus.<Envelop>request(address, dataRes.result(), deliveryOptions).onComplete(handler -> {
                    final Envelop response;
                    if (handler.succeeded()) {
                        /*
                         * // One Way message
                         * Only TRUE returned.
                         */
                        response = Envelop.success(Boolean.TRUE, SPI.V_STATUS.ok204());
                    } else {
                        response = this.failure(address, handler);
                    }
                    AckFlow.reply(context, response, event);
                });
            });
        }, context, event);
    }
}
