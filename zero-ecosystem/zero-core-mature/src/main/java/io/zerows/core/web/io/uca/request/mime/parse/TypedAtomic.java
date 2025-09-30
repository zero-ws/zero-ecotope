package io.zerows.core.web.io.uca.request.mime.parse;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.invocation.uca.parameter.ParameterBuilder;
import io.zerows.core.web.model.atom.Epsilon;

@SuppressWarnings("unchecked")
public class TypedAtomic<T> implements Atomic<T> {
    @Override
    public Epsilon<T> ingest(final RoutingContext context,
                             final Epsilon<T> income)
        throws WebException {
        final Class<?> paramType = income.getArgType();
        // Old:  TypedArgument.analyzeAgent
        final ParameterBuilder<RoutingContext> builder = ParameterBuilder.ofAgent();
        final Object returnValue = builder.build(context, paramType);
        return null == returnValue ? income.setValue(null) : income.setValue((T) returnValue);
    }
}
