package io.zerows.epoch.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SessionData {
    String value();

    /**
     * If the return type is JsonObject, you can extract one field to findRunning into session
     *
     * @return stored key in session
     */
    String field() default "";
}
