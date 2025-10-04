package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.ParameterBuilder;
import io.zerows.cortex.metadata.WebEpsilon;

@SuppressWarnings("unchecked")
public class AtomicTyped<T> implements Atomic<T> {
    @Override
    public WebEpsilon<T> ingest(final RoutingContext context,
                                final WebEpsilon<T> income)
        throws WebException {
        final Class<?> paramType = income.getArgType();
        // Old:  TypedArgument.analyzeAgent
        final ParameterBuilder<RoutingContext> builder = ParameterBuilder.ofAgent();
        final Object returnValue = builder.build(context, paramType);
        return null == returnValue ? income.setValue(null) : income.setValue((T) returnValue);
    }
}
