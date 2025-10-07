package io.zerows.platform.annotations.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cc架构中的标注（通常在组件内部使用）
 *
 * @author lang : 2023/4/30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Memory {
    Class<?> value();      // 缓存的类
}
