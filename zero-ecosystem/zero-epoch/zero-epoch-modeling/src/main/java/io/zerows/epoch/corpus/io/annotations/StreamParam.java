package io.zerows.epoch.corpus.io.annotations;


import io.zerows.epoch.corpus.io.uca.response.resolver.UnsetResolver;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StreamParam {
    /**
     * Default resolver to process the stream regionInput
     */
    Class<?> resolver() default UnsetResolver.class;
}
