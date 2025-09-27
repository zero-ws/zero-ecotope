package io.zerows.core.web.io.exception;

import io.zerows.ams.constant.em.app.HttpStatusCode;
import io.zerows.core.exception.WebException;
import io.zerows.core.util.Ut;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

public class _415MediaNotSupportException extends WebException {

    public _415MediaNotSupportException(final Class<?> clazz,
                                        final MediaType media,
                                        final Set<MediaType> expected) {
        super(clazz, media.toString(), Ut.fromJoin(expected.toArray(new MediaType[]{})));
    }

    @Override
    public int getCode() {
        return -60006;
    }

    @Override
    public HttpStatusCode getStatus() {
        return HttpStatusCode.UNSUPPORTED_MEDIA_TYPE;
    }
}
