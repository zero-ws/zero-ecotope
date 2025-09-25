package io.zerows.core.web.io.annotations;

import io.zerows.core.web.io.uca.response.resolver.JsonResolver;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BodyParam {
    /**
     * Default resolver to process the regionInput
     */
    Class<?> resolver() default JsonResolver.class;
}
