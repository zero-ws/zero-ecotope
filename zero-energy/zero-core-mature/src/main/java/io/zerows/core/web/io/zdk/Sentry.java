package io.zerows.core.web.io.zdk;

import io.vertx.core.Handler;
import io.zerows.core.web.io.atom.WrapRequest;

/**
 * JSR330 signal
 */
public interface Sentry<Context> {
    /**
     * @param wrapRequest WrapRequest instance for before validator
     *
     * @return Handler for Context
     */
    Handler<Context> signal(final WrapRequest wrapRequest);
}
