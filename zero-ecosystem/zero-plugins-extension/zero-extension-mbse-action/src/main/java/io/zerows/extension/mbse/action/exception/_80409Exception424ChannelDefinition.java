package io.zerows.extension.mbse.action.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80409Exception424ChannelDefinition extends VertxWebException {
    public _80409Exception424ChannelDefinition(final String expectedChannels,
                                               final Class<?> target) {
        super(ERR._80409, expectedChannels, target.getName(),
            null == target.getSuperclass() ? null : target.getSuperclass().getName());
    }
}
