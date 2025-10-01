package io.zerows.extension.mbse.action.uca.param;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.eon.em.ParamMode;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;

import java.util.function.Supplier;

/*
 * package scope,
 * /api/xxx/:name?email=lang.yu@hp.com
 *
 * Parsed uri and query string both
 * -->
 *    name = xxx
 *    email = lang.yu@hp.com
 */
class QueryIngest implements JtIngest {
    private transient final Supplier<JtIngest> supplier = Pool.INNER_INGEST.get(ParamMode.PATH);

    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        final JtIngest ingest = this.supplier.get();
        final Envelop envelop = ingest.in(context, uri);
        /*
         * JqTool extracting
         */
        final MultiMap queryParams = context.queryParams();
        queryParams.forEach(item -> envelop.value(item.getKey(), item.getValue()));
        return envelop;
    }
}
