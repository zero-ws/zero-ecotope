package io.zerows.module.metadata.exception;

import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.DaemonException;

public class DaemonFieldWrongException extends DaemonException {

    public DaemonFieldWrongException(final Class<?> clazz,
                                     final JsonObject data,
                                     final String field) {
        super(clazz, data.encode(), field);
    }

    @Override
    public int getCode() {
        return -10006;
    }
}
