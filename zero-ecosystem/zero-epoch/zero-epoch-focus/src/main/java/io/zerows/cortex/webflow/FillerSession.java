package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

public class FillerSession implements Filler {
    @Override
    public Object apply(final String name,
                        final Class<?> paramType,
                        final RoutingContext context) {
        final Session session = context.session();
        return session.get(name);
    }
}
