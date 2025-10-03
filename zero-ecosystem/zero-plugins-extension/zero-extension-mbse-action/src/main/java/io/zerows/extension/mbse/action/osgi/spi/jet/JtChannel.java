package io.zerows.extension.mbse.action.osgi.spi.jet;

import io.vertx.core.Future;
import io.zerows.epoch.web.Envelop;

/*
 * The channel, it will be selected by ChannelSelector,
 * Here are four modes:
 */
public interface JtChannel {

    /*
     * Bind current to `ZApi` object here, in this situation
     * Your defined channel could get `ZApi` in memory and then
     * All the configuration information came from `ZApi` instead of other place.
     * JtChannel bind(Api api);
     */

    /*
     * Async implementation
     */
    Future<Envelop> transferAsync(Envelop envelop);
}
