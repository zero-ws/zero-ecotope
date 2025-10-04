package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class AtomicStandard<T> implements Atomic<T> {

    @Override
    public WebEpsilon<T> ingest(final RoutingContext context,
                                final WebEpsilon<T> income)
        throws WebException {
        final Supplier<Filler> fillerFn = Filler.PARAMS.get(income.getAnnotation().annotationType());
        final Filler filler = fillerFn.get();
        final Object value = filler.apply(income.getName(), income.getArgType(), context);
        return null == value ? income.setValue(null) : income.setValue((T) value);
    }
}
