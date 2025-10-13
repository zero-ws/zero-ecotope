package io.zerows.extension.mbse.action.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.platform.enums.EmWeb;

/**
 * @author lang : 2025-09-30
 */
public class _80408Exception424ChannelConflict extends VertxWebException {
    public _80408Exception424ChannelConflict(final String componentName,
                                             final EmWeb.Channel channelType) {
        super(ERR._80408, componentName, channelType);
    }
}
