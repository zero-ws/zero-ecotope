package io.zerows.core.web.io.uca.response.resolver;

import io.zerows.core.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.io.zdk.mime.Resolver;
import io.zerows.core.web.model.atom.Epsilon;

public class XFormResolver<T> implements Resolver<T> {

    @Override
    public Epsilon<T> resolve(final RoutingContext context,
                              final Epsilon<T> income)
        throws WebException {
        return income;
    }
}
