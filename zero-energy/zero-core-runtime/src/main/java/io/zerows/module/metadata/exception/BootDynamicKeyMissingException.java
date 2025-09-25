package io.zerows.module.metadata.exception;

import io.vertx.core.json.JsonObject;
import io.zerows.core.exception.BootingException;

public class BootDynamicKeyMissingException extends BootingException {

    public BootDynamicKeyMissingException(final Class<?> clazz,
                                          final String key,
                                          final JsonObject data) {
        super(clazz, key, data);
    }

    @Override
    public int getCode() {
        return -10005;
    }
}
