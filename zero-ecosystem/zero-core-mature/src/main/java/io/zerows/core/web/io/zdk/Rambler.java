package io.zerows.core.web.io.zdk;

import io.zerows.core.exception.BootingException;

import java.lang.annotation.Annotation;

/**
 * Verification for epsilon
 */
public interface Rambler {

    void verify(Class<? extends Annotation> clazz,
                Class<?> type) throws BootingException;
}
