package io.zerows.corpus.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * # 「Co」Zero Critical internal handler handler
 *
 * Common handler to handle handler
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
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
