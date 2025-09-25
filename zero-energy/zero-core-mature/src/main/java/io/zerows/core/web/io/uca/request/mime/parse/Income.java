package io.zerows.core.web.io.uca.request.mime.parse;

import io.vertx.ext.web.RoutingContext;
import io.zerows.core.web.model.atom.Event;

/**
 * # 「Co」Zero Framework for MIME parsing
 *
 * Incoming message for request
 *
 * @param <T> generic class type
 */
public interface Income<T> {
    /**
     * request mime analyzing
     *
     * @param context RoutingContext environment here
     * @param event   Event definition for method declared.
     *
     * @return Extract `Tool` processing
     */
    T in(RoutingContext context, Event event);
}
