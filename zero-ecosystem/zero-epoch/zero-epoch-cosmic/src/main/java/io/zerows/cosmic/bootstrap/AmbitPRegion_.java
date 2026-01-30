package io.zerows.cosmic.bootstrap;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.extension.PlugRegion;
import io.zerows.epoch.jigsaw.ZeroPlugins;
import io.zerows.epoch.web.Envelop;

import java.util.Objects;

/**
 * @author lang : 2024-06-27
 */
public class AmbitPRegion_ implements Ambit {

    @Override
    public Future<Envelop> then(final RoutingContext context, final Envelop envelop) {
        final Vertx vertx = context.vertx();


        final PlugRegion region = ZeroPlugins.of(vertx).createPlugin(PlugRegion.class);
        if (Objects.isNull(region)) {
            return Future.succeededFuture(envelop);
        }


        return region.before(context, envelop);
    }
}
