package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.extension.module.mbseapi.metadata.JtUri;

/*
 * JtUri generate useful handler here
 */
public interface JtAim {
    /*
     * Main workflow here.
     */
    Handler<RoutingContext> attack(JtUri uri);
}
