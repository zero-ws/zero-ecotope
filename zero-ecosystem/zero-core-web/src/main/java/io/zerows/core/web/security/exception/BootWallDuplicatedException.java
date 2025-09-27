package io.zerows.core.web.security.exception;

import io.zerows.core.exception.BootingException;
import io.zerows.core.util.Ut;

import java.util.Set;

public class BootWallDuplicatedException extends BootingException {

    public BootWallDuplicatedException(final Class<?> classes,
                                       final Set<String> classNames) {
        super(classes, Ut.fromJoin(classNames));
    }

    @Override
    public int getCode() {
        return -40038;
    }
}
