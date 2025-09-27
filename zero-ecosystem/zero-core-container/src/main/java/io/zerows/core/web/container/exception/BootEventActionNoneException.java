package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;
import io.zerows.core.web.model.atom.Event;

public class BootEventActionNoneException extends BootingException {

    public BootEventActionNoneException(final Class<?> clazz,
                                        final Event event) {
        super(clazz, event.getPath());
    }

    @Override
    public int getCode() {
        return -40008;
    }
}
