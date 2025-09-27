package io.zerows.extension.mbse.action.exception;

import io.zerows.ams.constant.em.app.EmTraffic;
import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

/*
 * The channel type is not configured correctly
 */
public class _424ChannelConflictException extends WebException {

    public _424ChannelConflictException(final Class<?> clazz,
                                        final String componentName, final EmTraffic.Channel channelType) {
        super(clazz, componentName, channelType);
    }

    @Override
    public int getCode() {
        return -80408;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.FAILED_DEPENDENCY;
    }
}
