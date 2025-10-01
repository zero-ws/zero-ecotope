package io.zerows.epoch.corpus.container.uca.gateway;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.container.uca.mode.AbstractAim;
import io.zerows.epoch.corpus.io.atom.WrapRequest;
import io.zerows.epoch.corpus.io.zdk.Sentry;
import io.zerows.epoch.corpus.model.Rule;

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
