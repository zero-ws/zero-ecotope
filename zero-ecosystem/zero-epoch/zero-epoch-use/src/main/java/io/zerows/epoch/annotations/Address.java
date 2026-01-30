package io.zerows.epoch.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 🍵 「Zero Annotation」EventBus 地址定义
 * <p>
 * 该注解用于描述 EventBus 地址，在 Vert.x 中 EventBus 地址类型为 String 而非复杂的数据结构。
 * 当前注解只能应用于 **方法** 上，这意味着当前的 Action 开启了 EventBus 模式，支持异步操作：
 * </p>
 * <pre>
 * 1. 返回类型是 `Future`。
 * 2. 参数可以是 Message / Handler。
 * 3. 您可以同时使用回调模式（Callback Style）或 Future 模式（Future Style）编写代码。
 * </pre>
 * <p>
 * 在 Zero 框架中，存在两种使用 `@Address` 进行 EventBus 通信的场景。
 * </p>
 * <p>
 * 🚀 场景 1：标准 RESTful Api
 * <p>
 * 您可以在标准组件的异步模式中使用此注解：Agent/Worker。
 * </p>
 * <pre>
 * 1. 异步 Agent 应放在标注了 `@EndPoint` 的类中，并且方法应该使用 `@Address` 注解。
 * 2. 异步 Worker 应放在标注了 `@Queue` 的类中，并且方法应该使用 `@Address` 注解。
 * 3. Agent/Worker 必须成对出现（1:1），它们通过 `@Address` 中定义的一致地址进行通信。
 * </pre>
 * <p>
 * 🚀 场景 2：订阅输入
 * <p>
 * 您可以在标注了 `@Subscribe` 的 WebSocket 方法中使用此注解。
 * </p>
 * <pre>
 * 1. WebSocket 注解 `@Subscribe` 不能出现在标注了 `@Queue` 的类中。
 * 2. 在 WebSocket 方法（由 `@Subscribe` 标注）中，`@Address` 意味着当前方法的输入来自该地址，同时也支持异步操作。
 * </pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Address {
    /**
     * Agent/Worker 之间通信的 EventBus 地址。
     *
     * @return EventBus 上的 String 地址
     */
    String value();
}
