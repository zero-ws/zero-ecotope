package io.zerows.epoch.corpus.io.uca.response.resolver;

import io.r2mo.typed.exception.WebException;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.io.zdk.mime.Resolver;
import io.zerows.epoch.corpus.model.Epsilon;

public class BufferResolver<T> implements Resolver<T> {

    @Override
    @SuppressWarnings("all")
    public Epsilon<T> resolve(final RoutingContext context,
                              final Epsilon<T> income)
        throws WebException {
        final Class<?> clazz = income.getArgType();
        if (Buffer.class == clazz) {
            final Buffer body = context.body().buffer();
            income.setValue((T) body);
        }
        return income;
    }
}
