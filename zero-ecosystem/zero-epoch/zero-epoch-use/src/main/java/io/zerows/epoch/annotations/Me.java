package io.zerows.epoch.annotations;

import io.zerows.platform.enums.modeling.EmValue;

import java.lang.annotation.*;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Me {
    EmValue.Bool active() default EmValue.Bool.TRUE;

    boolean app() default false;
}
