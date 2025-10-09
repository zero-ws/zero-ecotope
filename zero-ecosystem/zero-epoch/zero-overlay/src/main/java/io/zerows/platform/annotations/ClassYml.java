package io.zerows.platform.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 除开 Ym 之外的类用来标记旧版的类信息用于做 YmlClass 的序列化处理
 *
 * @author lang : 2025-10-05
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ClassYml {
}
