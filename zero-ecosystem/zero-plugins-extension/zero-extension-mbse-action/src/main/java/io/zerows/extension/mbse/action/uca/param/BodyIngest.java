package io.zerows.extension.mbse.action.uca.param;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.program.Ut;
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
 *    BODY: body information in current ingest
 */
class BodyIngest implements JtIngest {
    private transient final Supplier<JtIngest> supplier = Pool.INNER_INGEST.get(ParamMode.QUERY);

    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        final JtIngest ingest = this.supplier.get();
        final Envelop envelop = ingest.in(context, uri);
        /*
         * Body processing
         */
        final String body = context.body().asString();
        if (Ut.isJArray(body)) {
            // JsonArray格式
            envelop.value(KWeb.ARGS.PARAM_BODY, new JsonArray(body));
        } else if (Ut.isJObject(body)) {
            // JsonObject格式
            envelop.value(KWeb.ARGS.PARAM_BODY, new JsonObject(body));
        } else {
            // String格式
            envelop.value(KWeb.ARGS.PARAM_BODY, body);
        }
        return envelop;
    }
}
