package io.zerows.core.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Contract {
    /*
     * This annotation is used between different income component
     */
}
