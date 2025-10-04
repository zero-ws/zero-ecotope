package io.zerows.cosmic.bootstrap;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cortex.metadata.WebRule;

import java.util.List;
import java.util.Map;

/**
 * Major execution to verify the result.
 */
public class SentryVerifier extends AimBase implements Sentry<RoutingContext> {

    @Override
    public Handler<RoutingContext> signal(final WebRequest wrapRequest) {
        // continue to verify JsonObject/JsonArray type
        final Map<String, List<WebRule>> rulers
            = this.verifier().buildRulers(wrapRequest);
        return (context) -> this.executeRequest(context, rulers, wrapRequest);
    }
}
