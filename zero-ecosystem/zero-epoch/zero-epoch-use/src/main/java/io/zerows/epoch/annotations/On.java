package io.zerows.epoch.annotations;

import io.zerows.constant.VString;
import io.zerows.epoch.constant.DefaultClass;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface On {
    /*
     * Start job for the job input definition
     * - value: EventBus address
     * - income: income Implement class defined by `JobIncome`
     */
    String address() default VString.EMPTY;

    Class<?> income() default DefaultClass.class;
}
