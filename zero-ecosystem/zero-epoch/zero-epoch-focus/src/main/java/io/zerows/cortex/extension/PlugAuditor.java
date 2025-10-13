package io.zerows.cortex.extension;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.specification.atomic.HPlug;

/**
 * 「Extension」:
 * Name: Auditor System
 * Extension for auditing system in zero system
 * This sub system often happened before each Worker component.
 * It's configured in vertx-tp.yml file and will be triggered before
 * any worker method invoking.
 */
public interface PlugAuditor extends HPlug {

    /*
     * The object envelop should be modified in current method,
     * There is no default implementation in zero system.
     */
    Future<Envelop> audit(RoutingContext context, Envelop envelop);
}
