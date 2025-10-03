package io.zerows.epoch.corpus.container.uca.gateway;

import io.vertx.ext.web.RoutingContext;
import io.zerows.support.Ut;
import io.zerows.epoch.corpus.io.atom.WrapRequest;
import io.zerows.epoch.corpus.io.zdk.Sentry;

/**
 * Validation for request based on JSR303 Bean Validation
 * 1. Basic Parameters: @QueryParam, @PathParam
 * 2. Extend Parameters: @BodyParam -> JsonObject, JsonArray
 * 3. POJO Parameters: @BodyParam -> POJO
 */
public class SplitterSentry {

    public Sentry<RoutingContext> distribute(final WrapRequest wrapRequest) {
        // Annotation to different verifier workflow
        // In current situation, there is only one implementation to build StandardVerifier
        // In future we could extend this implementation
        return Ut.singleton(StandardVerifier.class);
    }
}
