package io.zerows.epoch.annotations;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Format {
    // 是否自由响应格式
    boolean freedom() default false;

    // 是否开启 smart 模式从 agent -> eventbus 传递数据
    boolean smart() default false;
}
