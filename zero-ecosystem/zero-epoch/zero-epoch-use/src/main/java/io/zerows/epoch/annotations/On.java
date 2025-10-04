package io.zerows.epoch.annotations;

import io.zerows.epoch.metadata.KEmptyInstance;
import io.zerows.platform.constant.VString;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

    Class<?> income() default KEmptyInstance.class;
}
