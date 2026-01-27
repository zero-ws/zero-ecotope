package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.jigsaw.NodeStore;
import io.zerows.epoch.web.Envelop;
import io.zerows.epoch.web.WebEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class AimAStandard extends AimAsync implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Build future ( data handler )
             */
            final Future<Envelop> future = this.invokeAsync(context, event);
            /*
             * Event bus building / findRunning from vertx instance.
             */
            final Vertx vertx = context.vertx();
            final EventBus bus = vertx.eventBus();
            final String address = this.address(event);

            /*
             * New method instead of old
             * -- request(address, Tool, handler)
             */
            future.onComplete(dataRes -> {
                /*
                 * To avoid null dot result when the handler triggered result here
                 * SUCCESS
                 */
                if (dataRes.failed()) {
                    final Throwable error = dataRes.cause();
                    if (Objects.nonNull(error)) {
                        log.error(error.getMessage(), error);
                    }
                    /*
                     * Error Replying
                     */
                    AckFlow.reply(context, Envelop.failure(error));
                    return;
                }

                /*
                 * Before send, captured the previous, only this kind of situation need
                 * Do `combine` request here.
                 * Because:
                 *
                 * - In OneWay, the client do not focus join response data.
                 * - In Ping, the client also findRunning `true/false` only
                 * - In Sync, not need to pass Envelop join event bus
                 */
                final Envelop request = dataRes.result();

                final DeliveryOptions deliveryOptions = NodeStore.ofDelivery(vertx);
                bus.<Envelop>request(address, request, deliveryOptions).onComplete(handler -> {
                    final Envelop response;
                    if (handler.succeeded()) {
                        // Request - Response message
                        response = this.success(address, handler);
                    } else {
                        response = this.failure(address, handler);
                    }
                    // Request -> Response
                    response.from(request);
                    AckFlow.reply(context, response, event);
                });
            });
        }, context, event);
    }
}
