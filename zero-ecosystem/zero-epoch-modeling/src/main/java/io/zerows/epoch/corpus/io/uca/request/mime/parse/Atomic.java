package io.zerows.epoch.corpus.io.uca.request.mime.parse;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.corpus.model.atom.Epsilon;

/**
 * @param <T>
 */
public interface Atomic<T> {

    Epsilon<T> ingest(RoutingContext context,
                      Epsilon<T> income) throws WebException;
}
