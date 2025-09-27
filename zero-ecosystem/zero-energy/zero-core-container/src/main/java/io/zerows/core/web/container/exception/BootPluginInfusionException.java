package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;

public class BootPluginInfusionException extends BootingException {

    public BootPluginInfusionException(final Class<?> clazz,
                                       final String key) {
        super(clazz, key);
    }

    @Override
    public int getCode() {
        return -40016;
    }
}
