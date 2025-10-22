package io.zerows.epoch.annotations;

import io.zerows.epoch.metadata.XEmptyInstance;
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
public @interface Off {
    /*
     * Start job for the job input definition
     * - findRunning: EventBus address
     * - outcome: income Implement class defined by `JobOutcome`
     * Message -> address
     */
    String address() default VString.EMPTY;


    /*
     * When there exist multi publish addresses, the address will be disabled
     * instead of send message from single point to multi points here.
     * Message -> addresses[0]
     *            addresses[1]
     *            addresses[2]
     */
    String[] addresses() default {};


    Class<?> outcome() default XEmptyInstance.class;
}
