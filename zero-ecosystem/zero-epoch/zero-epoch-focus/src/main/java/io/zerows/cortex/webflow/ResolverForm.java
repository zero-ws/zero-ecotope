package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

public class ResolverForm<T> implements Resolver<T> {
    @Override
    public WebEpsilon<T> resolve(final RoutingContext context, final WebEpsilon<T> income) {
        return null;
    }
}
