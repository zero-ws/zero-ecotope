package io.zerows.extension.module.mbseapi.component;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.mbseapi.common.em.ParamMode;
import io.zerows.extension.module.mbseapi.metadata.JtUri;
import io.zerows.support.Ut;

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
class JtIngestBody implements JtIngest {
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
