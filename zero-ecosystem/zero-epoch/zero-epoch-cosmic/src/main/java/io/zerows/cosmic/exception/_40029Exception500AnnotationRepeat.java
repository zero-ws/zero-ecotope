package io.zerows.cosmic.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author lang : 2025-09-30
 */
public class _40029Exception500AnnotationRepeat extends VertxBootException {

    public _40029Exception500AnnotationRepeat(final Method method,
                                              final Class<? extends Annotation> annoCls,
                                              final int occurs) {
        super(ERR._40029, method.getName(), "@" + annoCls.getSimpleName(), occurs);
    }
}
