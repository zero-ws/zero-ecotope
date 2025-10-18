package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.sdk.Aim;
import io.zerows.epoch.basicore.WebEvent;
import io.zerows.epoch.web.Envelop;
import lombok.extern.slf4j.Slf4j;

/**
 * SyncAim: Non-Event Bus: Request-Response
 */
@Slf4j
public class AimSync extends AimBase implements Aim<RoutingContext> {
    @Override
    public Handler<RoutingContext> attack(final WebEvent event) {
        return (context) -> this.exec(() -> {
            /*
             * Build arguments
             */
            final Object[] arguments = this.buildArgs(context, event);
            /*
             * Method callxx
             * Java reflector to call defined method.
             */
            try {
                final Object result = this.invoke(event, arguments);

                // 3. Resource model building
                // final Envelop data = Flower.continuous(configure, result);
                /*
                 * Data handler to process Flower next result here.
                 */
                final Future<Envelop> future = AckThen.next(context, result);
                future.onComplete(dataRes -> {
                    /*
                     * To avoid null dot result when the handler triggered result here
                     * SUCCESS
                     */
                    if (dataRes.succeeded()) {
                        /*
                         * Reply future result directly here.
                         */
                        AckFlow.reply(context, dataRes.result(), event);
                    } else {
                        /*
                         * Reply error here
                         */
                        log.error("[ ZERO ] 调用异常：", dataRes.cause());
                        AckFlow.reply(context, Envelop.failure(dataRes.cause()));
                    }
                });
            } catch (final Throwable ex) {
                /*
                 * Reply error here
                 */
                final Envelop envelop = Envelop.failure(ex);
                AckFlow.reply(context, envelop);
            }

        }, context, event);
    }
}
