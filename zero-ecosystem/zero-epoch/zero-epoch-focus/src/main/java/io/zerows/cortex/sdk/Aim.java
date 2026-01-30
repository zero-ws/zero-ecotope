package io.zerows.cortex.sdk;

import io.vertx.core.Handler;
import io.zerows.epoch.web.WebEvent;

/**
 * Hunt to aim and select the objective
 */
public interface Aim<Context> {
    /**
     * @param event Scanned Event definition here
     * @return Handler for `RoutingContext`
     */
    Handler<Context> attack(final WebEvent event);
}
