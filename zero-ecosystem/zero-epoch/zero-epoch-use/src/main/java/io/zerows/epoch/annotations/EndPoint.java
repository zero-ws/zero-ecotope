package io.zerows.epoch.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marked as route, every vertx route must contains
 * <p>
 * io.vertx.up.annotations.Routine annotation to avoid scan method directly
 * 1. Each route must be marked as EndPoint;
 * 2. Each routine class should be pojo as JSR311;
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EndPoint {
}
