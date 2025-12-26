package io.zerows.extension.skeleton.common;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.management.OCacheUri;
import io.zerows.epoch.metadata.KView;
import io.zerows.extension.skeleton.spi.ScOrbit;
import io.zerows.spi.HPI;
import lombok.extern.slf4j.Slf4j;

/*
 * Key generated for uniform app.zero.cloud
 */
@Slf4j
class KeCache {

    static String keyView(final String method, final String uri, final KView view) {
        /*
         * session-POST:uri:position/name
         */
        return "session-" + method + ":" + uri + ":" + view.position() + "/" + view.view();
    }

    static String keyAuthorized(final String method, final String uri) {
        return "authorized-" + method + ":" + uri;
    }

    static String keyResource(final String method, final String uri) {
        return "resource-" + method + ":" + uri;
    }

    static String uri(final String uri, final String requestUri) {
        final JsonObject parameters = new JsonObject();
        parameters.put(KName.URI, uri);
        parameters.put(KName.URI_REQUEST, requestUri);
        // SPI: ScOrbit
        return HPI.of(ScOrbit.class).waitUntil(
            orbit -> orbit.analyze(parameters),
            () -> uri
        );
    }

    static String uri(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        final HttpMethod method = request.method();
        final String requestUri = OCacheUri.Tool.recovery(request.path(), method);
        return uri(requestUri, request.path());
    }

    static String keyView(final RoutingContext context) {
        final HttpServerRequest request = context.request();
        final String uri = uri(context);
        /* Cache Data */
        final String literal = request.getParam(KName.VIEW);
        /* Url Encoding / Decoding */
        final KView vis = KView.create(literal);
        final String cacheKey = keyView(request.method().name(), uri, vis);
        /* Cache Data */
        log.debug("{} 输入视图 View = {} / By = {}, 尝试命中缓存：uri = {}, method = {}",
            KeConstant.K_PREFIX_WEB, literal, cacheKey, uri, request.method().name());
        return cacheKey;
    }
}
