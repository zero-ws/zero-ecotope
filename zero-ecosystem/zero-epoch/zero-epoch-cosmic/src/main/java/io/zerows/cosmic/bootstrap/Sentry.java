package io.zerows.cosmic.bootstrap;

import io.vertx.core.Handler;
import io.zerows.cortex.metadata.WebRequest;

/**
 * JSR330 signal
 */
public interface Sentry<Context> {
    /**
     * @param wrapRequest WrapRequest instance for before validator
     *
     * @return Handler for Context
     */
    Handler<Context> signal(final WebRequest wrapRequest);
}
