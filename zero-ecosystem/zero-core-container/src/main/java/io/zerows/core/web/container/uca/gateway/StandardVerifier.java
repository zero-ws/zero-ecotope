package io.zerows.core.web.container.uca.gateway;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.container.uca.mode.AbstractAim;
import io.zerows.core.web.io.atom.WrapRequest;
import io.zerows.core.web.io.zdk.Sentry;
import io.zerows.core.web.model.atom.Rule;

import java.util.List;
import java.util.Map;

/**
 * Major execution to verify the result.
 */
public class StandardVerifier extends AbstractAim implements Sentry<RoutingContext> {

    @Override
    public Handler<RoutingContext> signal(final WrapRequest wrapRequest) {
        // continue to verify JsonObject/JsonArray type
        final Map<String, List<Rule>> rulers
            = this.verifier().buildRulers(wrapRequest);
        return (context) -> this.executeRequest(context, rulers, wrapRequest);
    }
}
