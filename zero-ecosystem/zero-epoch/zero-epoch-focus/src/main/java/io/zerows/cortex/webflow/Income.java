package io.zerows.cortex.webflow;

import io.vertx.ext.web.RoutingContext;
import io.zerows.epoch.web.WebEvent;

/**
 * # 「Co」Zero Framework for MIME parsing
 * <p>
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
     * @return Extract `Tool` processing
     */
    T in(RoutingContext context, WebEvent event);
}
