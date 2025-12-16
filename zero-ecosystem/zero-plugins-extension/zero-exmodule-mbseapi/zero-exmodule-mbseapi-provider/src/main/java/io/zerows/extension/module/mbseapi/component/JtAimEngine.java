package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.bootstrap.AckFlow;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

/*
 * Routing engine generation for Script Engine.
 */
public class JtAimEngine implements JtAim {
    private transient final JtMonitor monitor = JtMonitor.create(this.getClass());

    @Override
    public Handler<RoutingContext> attack(final JtUri uri) {
        return context -> {
            /*
             * Async info information in this step
             */
            final Envelop request = AckFlow.previous(context);
            final JsonObject data = request.data();
            this.monitor.aimEngine(uri.method(), uri.path(), data);
            /*
             * Next step
             * Resolution for next step data stored into envelop
             */
            AckFlow.next(context, request);
        };
    }
}
