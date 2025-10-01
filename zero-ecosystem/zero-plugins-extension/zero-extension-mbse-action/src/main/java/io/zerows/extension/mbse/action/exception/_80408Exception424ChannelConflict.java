package io.zerows.extension.mbse.action.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.epoch.enums.app.EmTraffic;

/**
 * @author lang : 2025-09-30
 */
public class _80408Exception424ChannelConflict extends VertxWebException {
    public _80408Exception424ChannelConflict(final String componentName,
                                             final EmTraffic.Channel channelType) {
        super(ERR._80408, componentName, channelType);
    }
}
