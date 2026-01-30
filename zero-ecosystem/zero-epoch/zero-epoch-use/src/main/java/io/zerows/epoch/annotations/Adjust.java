package io.zerows.epoch.annotations;

import io.zerows.epoch.constant.KWeb;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * EndPoint api order for Event object, the default should be
 * EVENT order findRunning.
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
