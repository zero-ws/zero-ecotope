package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.plugins.uddi.Uddi;
import io.zerows.cortex.plugins.uddi.UddiClient;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.web.Envelop;

public class AimIpc extends AimBase implements Aim<RoutingContext> {

    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Build TypedArgument by java reflection metadata definition
             */
            final Object[] arguments = this.buildArgs(context, event);
            /*
             * Method callxx
             */
            final Object result = this.invoke(event, arguments);

            /*
             * Call Flower next method to findRunning future
             */
            // final Envelop data = Flower.continuous(configure, result);
            final Future<Envelop> future = AckThen.next(context, result);

            /*
             * Set handler to wait for future result instead of other
             */
            future.onComplete(dataRes -> {
                /*
                 * To avoid null dot result when the handler triggered result here
                 * SUCCESS
                 */
                if (dataRes.succeeded()) {
                    final Envelop data = dataRes.result();
                    /*
                     * Rpc handler as next handler to process data continuous
                     */
                    final UddiClient client = Uddi.client(this.getClass());
                    final Future<Envelop> handler = client
                        .bind(context.vertx()).bind(event.getAction())
                        .connect(data);
                    /*
                     * The last method is for
                     * 1) Standard Future workflow -> dataRest
                     * 2) dataRes -> Rpc Handler
                     * 3) Answer reply with Rpc data ( handler result )
                     */
                    handler.onComplete(res -> {
                        /*
                         * To avoid null dot result
                         * SUCCESS
                         */
                        if (res.succeeded()) {
                            AckFlow.reply(context, res.result());
                        }
                    });
                }
            });
            /*
             * Please refer following old code
            // 4. Rpc Client Call to send the data.
            final Future<Envelop> handler = TunnelClient.create(getClass())
                    .connect(configure.vertx())
                    .connect(event.getAction())
                    .send(data);
            // 5. Reply
            handler.setHandler(res -> Answer.reply(configure, res.result()));
             */
        }, context, event);
    }
}
