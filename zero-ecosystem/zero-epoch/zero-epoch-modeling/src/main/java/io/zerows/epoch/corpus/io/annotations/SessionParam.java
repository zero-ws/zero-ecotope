package io.zerows.epoch.corpus.io.annotations;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionParam {
    String value();
}
