package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.assembly.DiProxyInstance;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.web.Envelop;
import io.zerows.program.Ux;

import java.util.Objects;

public class AimAsync extends AimBase implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Build future ( data handler )
             */
            final Future<Envelop> future = this.invoke(context, event);
            /*
             * Event bus building / get from vertx instance.
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
                if (dataRes.succeeded()) {
                    /*
                     * Before send, captured the previous, only this kind of situation need
                     * Do `combine` request here.
                     * Because:
                     *
                     * - In OneWay, the client do not focus join response data.
                     * - In Ping, the client also get `true/false` only
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
                } else {
                    if (Objects.nonNull(dataRes.cause())) {
                        dataRes.cause().printStackTrace();
                    }
                    /*
                     * Error Replying
                     */
                    AckFlow.reply(context, Envelop.failure(dataRes.cause()));
                }
            });
        }, context, event);
    }

    private Future<Envelop> invoke(final RoutingContext context,
                                   final WebEvent event) {
        final Object proxy = event.getProxy();
        /*
         * Method arguments building here.
         */
        final Object[] arguments = this.buildArgs(context, event);
        /*
         * Whether it's interface mode or agent mode
         */
        final Future<Envelop> invoked;
        if (proxy instanceof DiProxyInstance) {
            final JsonObject message = new JsonObject();
            for (int idx = 0; idx < arguments.length; idx++) {
                message.put(String.valueOf(idx), arguments[idx]);
            }
            /*
             * Interface mode
             */
            invoked = AckThen.next(context, message);
        } else {
            /*
             * Agent mode
             */
            final Object returnValue = this.invoke(event, arguments);
            invoked = AckThen.next(context, returnValue);
        }

        return invoked.compose(response -> {
            /*
             * The next method of compose for future building assist data such as
             * Headers,
             * User,
             * Session
             * Context
             * It's critical for Envelop object when communication
             */
            response.bind(context);
            return Ux.future(response);
        });
    }
}
