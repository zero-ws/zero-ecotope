package io.zerows.extension.mbse.action.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80407Exception501ChannelError extends VertxWebException {
    public _80407Exception501ChannelError(final String channel) {
        super(ERR._80407, channel);
    }
}
