package io.zerows.cosmic.handler;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cosmic.bootstrap.AimAnswer;
import io.zerows.epoch.web.Envelop;
import io.zerows.program.Ux;
import lombok.extern.slf4j.Slf4j;

/**
 * Common handler to handle handler
 */
@Slf4j
public class EndurerAuthenticate implements Handler<RoutingContext> {

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
                log.info("[ ZERO ] Web Exception: {} = {}", ex.getClass().getName(), ex.getMessage());
                /*
                 * XHeader bind
                 */
                Ux.debug(error, () -> error);
                AimAnswer.reply(event, Envelop.failure(error));
            } else {
                // Other exception found
                log.info("[ ZERO ] Exception: {} = {}", ex.getClass().getName(), ex.getMessage());
                ex.printStackTrace();
                AimAnswer.reply(event, Envelop.failure(ex));
            }
        } else {
            // Success, do not throw, continue to request
            event.next();
        }
    }
}
