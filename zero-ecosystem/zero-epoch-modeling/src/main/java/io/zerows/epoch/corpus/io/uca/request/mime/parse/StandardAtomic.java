package io.zerows.epoch.corpus.io.uca.request.mime.parse;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.io.uca.request.argument.Filler;
import io.zerows.epoch.corpus.model.atom.Epsilon;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class StandardAtomic<T> implements Atomic<T> {

    @Override
    public Epsilon<T> ingest(final RoutingContext context,
                             final Epsilon<T> income)
        throws WebException {
        final Supplier<Filler> fillerFn = Filler.PARAMS.get(income.getAnnotation().annotationType());
        final Filler filler = fillerFn.get();
        final Object value = filler.apply(income.getName(), income.getArgType(), context);
        return null == value ? income.setValue(null) : income.setValue((T) value);
    }
}
