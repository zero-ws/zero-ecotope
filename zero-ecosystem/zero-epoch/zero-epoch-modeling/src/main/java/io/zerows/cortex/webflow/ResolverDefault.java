package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

public class ResolverDefault<T> implements Resolver<T> {

    @Override
    public WebEpsilon<T> resolve(final RoutingContext context,
                                 final WebEpsilon<T> income)
        throws WebException {
        // Buffer Resolver
        return income;
    }
}
