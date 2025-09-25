package io.zerows.core.web.io.zdk;

import io.vertx.core.Handler;
import io.zerows.core.web.model.atom.Event;

/**
 * Hunt to aim and select the objective
 */
public interface Aim<Context> {
    /**
     * @param event Scanned Event definition here
     *
     * @return Handler for `RoutingContext`
     */
    Handler<Context> attack(final Event event);
}
