package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

public class ResolverBuffer<T> implements Resolver<T> {

    @Override
    @SuppressWarnings("all")
    public WebEpsilon<T> resolve(final RoutingContext context,
                                 final WebEpsilon<T> income)
        throws WebException {
        final Class<?> clazz = income.getArgType();
        if (Buffer.class == clazz) {
            final Buffer body = context.body().buffer();
            income.setValue((T) body);
        }
        return income;
    }
}
