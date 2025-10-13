package io.zerows.epoch.annotations;

import io.zerows.specification.configuration.HConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 移除旧版的 Infix 架构，提取成最新版的 Actor 架构，每个不同的 Actor 在启动之后都会直接提取符合类型的客户端等相关信息
 * <pre>
 *     1. 初始化执行，替换原来的 Infix.init 方法
 *     2. 替换原来的 XxxClient 方法，可直接提取 -> 注入的方式
 * </pre>
 *
 * @author lang : 2025-10-13
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Actor {
    /**
     * 配置项的值，外层系统构造 {@link HConfig} 依赖此值来提取对应的配置
     *
     * @return 值
     */
    String value();

    /**
     * 梯度计算顺序，并且保证启动流程，区间说明
     * <pre>
     *     < 0: 系统内置 Actor，必须最先启动
     *     = 0: 普通 Actor，默认值
     *     > 0: 业务 Actor，最后启动
     * </pre>
     *
     * @return 顺序，默认 -1，负数优先级高于正数
     */
    int sequence() default -1;
}
