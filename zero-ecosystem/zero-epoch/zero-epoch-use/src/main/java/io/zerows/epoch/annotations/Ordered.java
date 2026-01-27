package io.zerows.epoch.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated on @WebFilter for manage sequence of class.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Ordered {
    /**
     * The order will be the sequence of filters
     *
     * @return The sequence of filters.
     */
    int value() default 0;
}
