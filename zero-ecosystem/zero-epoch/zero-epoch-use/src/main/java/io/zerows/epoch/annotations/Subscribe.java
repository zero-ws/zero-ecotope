package io.zerows.epoch.annotations;

import io.zerows.epoch.metadata.KEmptyInstance;
import io.zerows.platform.constant.VString;
import io.zerows.platform.enums.RemindType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation will be comments to `websocket` method and the
 * SockAxis will scan all these kind of `method` instead, because SockJS
 * is often building just like following:
 * 1) The Server should send the message to client, here are the trigger mode
 * -- A. The scheduled @Job send the message to address
 * -- B. The api send the message to address ( One-Way Mode )
 * 2) The Address could be bind in front-end application and get the message from event bus.
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Subscribe {
    /*
     * The first part of `websocket`, the address means that
     * the correct address from front-end, here are the address design for
     * socket.
     *
     * As workflow
     *
     * 1. The job will action and send the message to `address`,
     *    the `address` will be bind to EventBus
     * 2. The critical `address` is stored into `value` for Router mounting
     */
    String name() default VString.EMPTY;

    Class<?> input() default KEmptyInstance.class;

    String value();

    boolean secure() default true;

    RemindType type() default RemindType.REMIND;
}
