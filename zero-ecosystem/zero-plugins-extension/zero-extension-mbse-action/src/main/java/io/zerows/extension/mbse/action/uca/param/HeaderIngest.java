package io.zerows.extension.mbse.action.uca.param;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KWeb;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;

@Deprecated
class HeaderIngest implements JtIngest {
    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        /* Header */
        final MultiMap headers = context.request().headers();
        final JsonObject headerData = new JsonObject();
        headers.names().stream()
            .filter(field -> field.startsWith(KWeb.HEADER.PREFIX))
            .forEach(field -> headerData.put(field, headers.get(field)));
        return Envelop.success(new JsonObject().put(KWeb.ARGS.PARAM_HEADER, headerData));
    }
}
