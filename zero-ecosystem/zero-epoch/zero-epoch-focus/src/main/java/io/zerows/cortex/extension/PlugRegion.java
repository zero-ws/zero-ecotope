package io.zerows.cortex.extension;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.specification.atomic.HPlug;

/**
 * 「Extension」
 * Name: Data Region
 * Data Region when you want to do some modification on Envelop,
 * There are two position to process region modification:
 * 1) Before Envelop request building / sending.
 * 2) After Response replying from agent.
 * This plugin exist in agent only and could not be used in worker, standard
 */
public interface PlugRegion extends HPlug {
    /*
     * Request processing
     */
    Future<Envelop> before(RoutingContext context, Envelop request);

    /*
     * Response processing
     */
    Future<Envelop> after(RoutingContext context, Envelop response);
}
