package io.zerows.extension.mbse.action.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;

public class _501ChannelErrorException extends WebException {

    public _501ChannelErrorException(final Class<?> clazz,
                                     final String channelName) {
        super(clazz, channelName);
    }

    @Override
    public int getCode() {
        return -80407;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.NOT_IMPLEMENTED;
    }
}
