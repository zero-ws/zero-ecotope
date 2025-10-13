package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.extension.PlugAuditor;
import io.zerows.epoch.configuration.ZeroPlugins;
import io.zerows.epoch.web.Envelop;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class AmbitPAudit_ implements Ambit {
    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        final Vertx vertx = context.vertx();


        final PlugAuditor auditor = ZeroPlugins.of(vertx).createPlugin(PlugAuditor.class);
        if (Objects.isNull(auditor)) {
            return Future.succeededFuture(envelop);
        }


        return auditor.audit(context, envelop);
    }
}
