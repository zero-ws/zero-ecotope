package io.zerows.cosmic.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class EndurerCommon implements Handler<RoutingContext> {

    private EndurerCommon() {
    }

    public static Handler<RoutingContext> create() {
        return new EndurerCommon();
    }

    @Override
    public void handle(final RoutingContext event) {
        if (event.failed()) {
            /*
             * Reply
             */
            final Throwable error = event.failure();
            error.printStackTrace();
        }
    }
}
