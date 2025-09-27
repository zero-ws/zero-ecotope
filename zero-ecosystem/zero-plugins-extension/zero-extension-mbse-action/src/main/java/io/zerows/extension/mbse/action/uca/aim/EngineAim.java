package io.zerows.extension.mbse.action.uca.aim;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.container.uca.mode.Answer;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.uca.monitor.JtMonitor;

/*
 * Routing engine generation for Script Engine.
 */
public class EngineAim implements JtAim {
    private transient final JtMonitor monitor = JtMonitor.create(this.getClass());

    @Override
    public Handler<RoutingContext> attack(final JtUri uri) {
        return context -> {
            /*
             * Async log information in this step
             */
            final Envelop request = Answer.previous(context);
            final JsonObject data = request.data();
            this.monitor.aimEngine(uri.method(), uri.path(), data);
            /*
             * Next step
             * Resolution for next step data stored into envelop
             */
            Answer.next(context, request);
        };
    }
}
