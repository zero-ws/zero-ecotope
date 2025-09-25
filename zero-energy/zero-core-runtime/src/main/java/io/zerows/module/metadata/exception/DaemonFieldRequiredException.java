package io.zerows.module.metadata.exception;

import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.DaemonException;

public class DaemonFieldRequiredException extends DaemonException {

    public DaemonFieldRequiredException(final Class<?> clazz,
                                        final JsonObject data,
                                        final String field) {
        super(clazz, data.encode(), field);
    }

    @Override
    public int getCode() {
        return -10002;
    }
}
