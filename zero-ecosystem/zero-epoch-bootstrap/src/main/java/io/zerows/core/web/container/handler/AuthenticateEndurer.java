package io.zerows.core.web.container.handler;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.uca.log.Annal;
import io.zerows.core.web.container.uca.mode.Answer;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.unity.Ux;

/**
 * Common handler to handle handler
 */
public class AuthenticateEndurer implements Handler<RoutingContext> {

    private static final Annal LOGGER = Annal.get(AuthenticateEndurer.class);

    private AuthenticateEndurer() {
    }

    public static Handler<RoutingContext> create() {
        return new AuthenticateEndurer();
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
                Answer.reply(event, Envelop.failure(error));
            } else {
                // Other exception found
                LOGGER.info("Exception: {0} = {1}", ex.getClass().getName(), ex.getMessage());
                ex.printStackTrace();
                Answer.reply(event, Envelop.failure(ex));
            }
        } else {
            // Success, do not throw, continue to request
            event.next();
        }
    }
}
