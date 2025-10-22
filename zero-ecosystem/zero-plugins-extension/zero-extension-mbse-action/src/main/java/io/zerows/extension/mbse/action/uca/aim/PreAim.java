package io.zerows.extension.mbse.action.uca.aim;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.bootstrap.AckFlow;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;

/**
 * The first handler in routing
 * 1. Host http header checking ( Not Support ）
 * 2. Analyzing required part ( required / contained )
 * 3. Analyzing parameters of api, findRunning parameters and web `Envelop`
 */
public class PreAim implements JtAim {
    @Override
    public Handler<RoutingContext> attack(final JtUri uri) {
        /* JtIngest in booting workflow instead of other position */
        final JtIngest ingest = JtIngest.getInstance();
        return context -> {
            /* Extract parameters */
            final Envelop envelop = ingest.in(context, uri);
            /* Last step for next */
            AckFlow.next(context, envelop);
        };
    }
}
