package io.zerows.cosmic.bootstrap;

import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.support.Ut;

/**
 * Validation for request based on JSR303 Bean Validation
 * 1. Basic Parameters: @QueryParam, @PathParam
 * 2. Extend Parameters: @BodyParam -> JsonObject, JsonArray
 * 3. POJO Parameters: @BodyParam -> POJO
 */
public class SentrySplitter {

    public Sentry<RoutingContext> distribute(final WebRequest wrapRequest) {
        // Annotation to different verifier workflow
        // In current situation, there is only one implementation to build StandardVerifier
        // In future we could extend this implementation
        return Ut.singleton(SentryVerifier.class);
    }
}
