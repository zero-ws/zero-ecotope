package io.zerows.core.web.io.uca.request.mime.parse;

import io.zerows.core.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.model.atom.Epsilon;

/**
 * @param <T>
 */
public interface Atomic<T> {

    Epsilon<T> ingest(RoutingContext context,
                      Epsilon<T> income) throws WebException;
}
