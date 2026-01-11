package io.zerows.cortex;

import io.vertx.core.eventbus.Message;
import io.zerows.cortex.exception._40047Exception500InvokerNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Worker 模式下的调用者路由处理，根据不同的方法签名直接路由到不同的调用者中实现完整分流模式
 * <pre>
 *     1. 旧版本的第二参使用了 {@link Class} 类型，此处拓展成全参数模式
 *     2. 追加了 {@link Message} 的全域调用模式，并且增强参数处理流程
 * </pre>
 */
public class InvokerGateway {

    public static Invoker invoker(final Method method) {
        // ----------------------- Void / void
        if (CallSpec.isRetVoid(method)) {
            /*
             * 「Async Support」
             * 方法返回值是：void/Void，您必须内部实现异步流程，并且在方法内部处理相关逻辑，
             * 还有一点，对于 message 类型的参数，必须在内部调用 reply，否则不会成功
             */
            if (CallSpec.isInEnvelop(method)) {
                // void method(Envelop)
                return Invoker.of(InvokerPing.class, method);
            }

            if (CallSpec.isInMessage(method)) {
                // void method(Message<Envelop>)
                return Invoker.of(InvokerMessage.class, method);
            }


            // void method(???)
            return Invoker.of(InvokerPingT.class, method);
        }


        // ----------------------- Envelop
        if (CallSpec.isRetEnvelop(method)) {
            /*
             * 「Sync Only」
             * 方法返回值是 Envelop，此操作是内部定义的标准同步操作，您不能在此模式下执行异步操作
             */
            if (CallSpec.isInEnvelop(method)) {
                // Envelop method(Envelop)
                return Invoker.of(InvokerSync.class, method);
            }


            // Envelop method(???)
            return Invoker.of(InvokerDim.class, method);
        }


        // ----------------------- Future
        if (CallSpec.isRetFuture(method)) {
            /*
             * 「Async Only」
             * 方法返回值必须是 Future，标准模式
             */
            if (CallSpec.isInEnvelop(method)) {
                // Future<?> method(Envelop)
                return Invoker.of(InvokerFuture.class, method);
            }


            // Future<T> method(I) 最高频的使用模式
            return Invoker.of(InvokerAsync.class, method);
        }


        // ----------------------- 参数不可以带有 Message
        if (!CallSpec.isInMessage(method)) {
            /*
             * 自由模式（推荐模式）
             * 自由模式采用了标准 Java 编程方法，但移除了下边几种特殊情况
             * 1. 返回值 = void/Void
             * 2. 返回值 = Envelop
             * 3. 返回值 = Future
             * 若想要执行这种模式，`Message` 就直接被禁用了，若想要异步则可直接返回 Future
             */
            return Invoker.of(InvokerDynamic.class, method);
        }
        final String parameters = Arrays.stream(method.getParameterTypes())
            .map(Class::getSimpleName) // 获取类名 (不含包名)
            .collect(Collectors.joining(", ", "[", "]")); // 拼接：分隔符, 前缀, 后缀
        throw new _40047Exception500InvokerNull(method.getReturnType(), parameters);
    }
}
