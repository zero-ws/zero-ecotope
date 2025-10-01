package io.zerows.epoch.corpus.security.annotations;

import io.zerows.epoch.enums.EmSecure;

import java.lang.annotation.*;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AuthorizedResource {
    EmSecure.AuthWord value() default EmSecure.AuthWord.AND;
}
