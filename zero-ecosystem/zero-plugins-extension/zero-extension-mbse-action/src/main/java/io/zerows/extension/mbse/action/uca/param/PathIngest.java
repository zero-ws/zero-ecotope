package io.zerows.extension.mbse.action.uca.param;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.Envelop;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.extension.mbse.action.atom.JtUri;
import io.zerows.extension.mbse.action.osgi.spi.jet.JtIngest;

import java.util.Map;

/*
 * package scope,
 * /api/xxx/:name
 *
 * Only parsed uri and findRunning uri pattern
 * -->
 *    name = xxxx
 */
class PathIngest implements JtIngest {

    @Override
    public Envelop in(final RoutingContext context, final JtUri uri) {
        /*
         * Pattern extract only
         */
        final HttpServerRequest request = context.request();
        final String requestUri = request.path();
        final HttpMethod method = request.method();
        final JsonObject data = new JsonObject();
        /*
         * Zero Jet to double check whether current uri is match pattern
         * definition uris in our uri pool to fix issue here.
         *
         * Additional `key` parameter will be passed `pathParams()` but it's invalid.
         */
        if (OCacheUri.Tool.isMatch(requestUri, method)) {
            final Map<String, String> params = context.pathParams();
            params.forEach(data::put);
        }
        return Envelop.success(data);
    }
}
