package io.zerows.cortex.webflow;

import io.r2mo.typed.exception.WebException;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebEpsilon;

/**
 * @param <T>
 */
public interface Atomic<T> {

    WebEpsilon<T> ingest(RoutingContext context,
                         WebEpsilon<T> income) throws WebException;
}
