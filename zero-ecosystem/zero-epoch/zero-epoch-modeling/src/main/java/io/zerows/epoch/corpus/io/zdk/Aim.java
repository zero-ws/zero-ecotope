package io.zerows.epoch.corpus.io.zdk;

import io.vertx.core.Handler;
import io.zerows.epoch.basicore.ActorEvent;

/**
 * Hunt to aim and select the objective
 */
public interface Aim<Context> {
    /**
     * @param event Scanned Event definition here
     *
     * @return Handler for `RoutingContext`
     */
    Handler<Context> attack(final ActorEvent event);
}
