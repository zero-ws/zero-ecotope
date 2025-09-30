package io.zerows.core.annotations;

import io.zerows.core.constant.KWeb;

import java.lang.annotation.*;

/**
 * EndPoint api order for Event object, the default should be
 * EVENT order value.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Adjust {
    /**
     * Annotated on Api method only
     */
    int value() default KWeb.ORDER.EVENT;
}
