package io.zerows.corpus.handler;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.component.log.Annal;
import io.zerows.corpus.container.AimAnswer;
import io.zerows.epoch.web.Envelop;
import io.zerows.program.Ux;

/**
 * Common handler to handle handler
 */
public class EndurerAuthenticate implements Handler<RoutingContext> {

    private static final Annal LOGGER = Annal.get(EndurerAuthenticate.class);

    private EndurerAuthenticate() {
    }

    public static Handler<RoutingContext> create() {
        return new EndurerAuthenticate();
    }

    @Override
    public void handle(final RoutingContext event) {
        if (event.failed()) {
            final Throwable ex = event.failure();
            if (ex instanceof final WebException error) {
                LOGGER.info("Web Exception: {0} = {1}", ex.getClass().getName(), ex.getMessage());
                /*
                 * XHeader bind
                 */
                Ux.debug(error, () -> error);
                AimAnswer.reply(event, Envelop.failure(error));
            } else {
                // Other exception found
                LOGGER.info("Exception: {0} = {1}", ex.getClass().getName(), ex.getMessage());
                ex.printStackTrace();
                AimAnswer.reply(event, Envelop.failure(ex));
            }
        } else {
            // Success, do not throw, continue to request
            event.next();
        }
    }
}
