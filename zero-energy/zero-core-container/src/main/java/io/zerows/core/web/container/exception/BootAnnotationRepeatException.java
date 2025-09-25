package io.zerows.core.web.container.exception;

import io.zerows.core.exception.BootingException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class BootAnnotationRepeatException extends BootingException {

    public BootAnnotationRepeatException(final Class<?> clazz,
                                         final Method method,
                                         final Class<? extends Annotation> annoCls,
                                         final int occurs) {
        super(clazz, method.getName(), "@" + annoCls.getSimpleName(), occurs);
    }

    @Override
    public int getCode() {
        return -40029;
    }
}
