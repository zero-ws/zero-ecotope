package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.bootstrap.AckFlow;
import io.zerows.cosmic.bootstrap.Ambit;
import io.zerows.cosmic.bootstrap.AmbitNext;
import io.zerows.epoch.configuration.NodeStore;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

import java.util.Objects;

public class JtAimSend implements JtAim {

    private transient final JtMonitor monitor = JtMonitor.create(this.getClass());

    @Override
    public Handler<RoutingContext> attack(final JtUri uri) {
        /*
         * 「Booting LifeCycle Cycle」
         *  Code Area
         */
        return context -> {
            /*
             * 「Request LifeCycle Cycle」
             */
            final Envelop request = AckFlow.previous(context);
            /*
             * Set id here, consumer will extract api data in worker
             */
            request.key(uri.key());
            /*
             * Mount the same extension / plug-in in web request
             */
            Ambit.of(AmbitNext.class).then(context, request).onComplete(res -> {
                if (res.succeeded()) {
                    final Envelop normalized = res.result();
                    final JsonObject data = normalized.data();
                    final String address = uri.worker().getWorkerAddress(); // Address
                    /*
                     * Monitor data and address
                     */
                    this.monitor.aimSend(data, address);

                    final Vertx vertx = context.vertx();
                    final EventBus event = vertx.eventBus();

                    final DeliveryOptions deliveryOptions = NodeStore.ofDelivery(vertx);
                    event.<Envelop>request(address, normalized, deliveryOptions).onComplete(handler -> {
                        if (handler.succeeded()) {
                            /*
                             * 「Success」
                             */
                            final Message<Envelop> result = handler.result();
                            AckFlow.reply(context, result.body(), uri::producesMime);
                        } else {
                            /*
                             * 「Failure」
                             */
                            final Envelop error = Envelop.failure(handler.cause());
                            AckFlow.reply(context, error);
                        }
                    });
                } else {
                    if (Objects.nonNull(res.cause())) {
                        res.cause().printStackTrace();
                    }
                }
            });

        };
    }
}
