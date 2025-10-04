package io.zerows.extension.mbse.action.uca.aim;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.basicore.NodeNetwork;
import io.zerows.epoch.corpus.container.uca.mode.Answer;
import io.zerows.epoch.corpus.container.uca.reply.ActionNext;
import io.zerows.epoch.corpus.container.uca.reply.OAmbit;
import io.zerows.epoch.management.OCacheNode;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.uca.monitor.JtMonitor;

import java.util.Objects;

public class SendAim implements JtAim {

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
            final Envelop request = Answer.previous(context);
            /*
             * Set id here, consumer will extract api data in worker
             */
            request.key(uri.key());
            /*
             * Mount the same extension / plug-in in web request
             */
            OAmbit.of(ActionNext.class).then(context, request).onComplete(res -> {
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

                    final NodeNetwork network = OCacheNode.of().network();
                    final DeliveryOptions deliveryOptions = network.get().optionDelivery();
                    event.<Envelop>request(address, normalized, deliveryOptions).onComplete(handler -> {
                        if (handler.succeeded()) {
                            /*
                             * 「Success」
                             */
                            final Message<Envelop> result = handler.result();
                            Answer.reply(context, result.body(), uri::producesMime);
                        } else {
                            /*
                             * 「Failure」
                             */
                            final Envelop error = Envelop.failure(handler.cause());
                            Answer.reply(context, error);
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
