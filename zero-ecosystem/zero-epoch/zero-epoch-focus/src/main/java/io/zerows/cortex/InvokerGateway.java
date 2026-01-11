package io.zerows.cortex;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zerows.cortex.exception._40047Exception500InvokerNull;
import io.zerows.epoch.web.Envelop;

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

    // Invoker Cache for Multi Thread
    public static final Cc<String, Invoker> CCT_INVOKER = Cc.openThread();

    private static boolean isRetVoid(final Class<?> returnType) {
        return void.class == returnType || Void.class == returnType;
    }

    private static boolean isRetEnvelop(final Class<?> returnType) {
        return Envelop.class == returnType;
    }

    private static boolean isRetFuture(final Class<?> returnType) {
        return Future.class.isAssignableFrom(returnType);
    }

    private static boolean isInEnvelop(final Class<?>[] paramCls) {
        return 1 == paramCls.length && Envelop.class == paramCls[0];
    }

    private static boolean isInMessage(final Class<?>[] paramCls) {
        return Arrays.stream(paramCls).anyMatch(Message.class::isAssignableFrom);
    }

    public static Invoker invoker(final Class<?> returnType,
                                  final Class<?>[] paramCls) {
        // ----------------------- Void / void
        if (isRetVoid(returnType)) {
            /*
             * 「Async Support」
             * 方法返回值是：void/Void，您必须内部实现异步流程，并且在方法内部处理相关逻辑，
             * 还有一点，对于 message 类型的参数，必须在内部调用 reply，否则不会成功
             */
            if (isInEnvelop(paramCls)) {
                // void method(Envelop)
                return CCT_INVOKER.pick(InvokerPing::new, InvokerPing.class.getName());
            }

            if (isInMessage(paramCls)) {
                // void method(Message<Envelop>)
                return CCT_INVOKER.pick(InvokerMessage::new, InvokerMessage.class.getName());
            }


            // void method(???)
            return CCT_INVOKER.pick(InvokerPingT::new, InvokerPingT.class.getName());
        }


        // ----------------------- Envelop
        if (isRetEnvelop(returnType)) {
            /*
             * 「Sync Only」
             * 方法返回值是 Envelop，此操作是内部定义的标准同步操作，您不能在此模式下执行异步操作
             */
            if (isInEnvelop(paramCls)) {
                // Envelop method(Envelop)
                return CCT_INVOKER.pick(InvokerSync::new, InvokerSync.class.getName());
            }


            // Envelop method(???)
            return CCT_INVOKER.pick(InvokerDim::new, InvokerDim.class.getName());
        }


        // ----------------------- Future
        if (isRetFuture(returnType)) {
            /*
             * 「Async Only」
             * 方法返回值必须是 Future，标准模式
             */
            if (isInEnvelop(paramCls)) {
                // Future<?> method(Envelop)
                return CCT_INVOKER.pick(InvokerFuture::new, InvokerFuture.class.getName());
            }


            // Future<T> method(I) 最高频的使用模式
            return CCT_INVOKER.pick(InvokerAsync::new, InvokerAsync.class.getName());
        }


        // ----------------------- 参数不可以带有 Message
        if (!isInMessage(paramCls)) {
            /*
             * 自由模式（推荐模式）
             * 自由模式采用了标准 Java 编程方法，但移除了下边几种特殊情况
             * 1. 返回值 = void/Void
             * 2. 返回值 = Envelop
             * 3. 返回值 = Future
             * 若想要执行这种模式，`Message` 就直接被禁用了，若想要异步则可直接返回 Future
             */
            return CCT_INVOKER.pick(InvokerDynamic::new, InvokerDynamic.class.getName());
        }
        final String parameters = Arrays.stream(paramCls)
            .map(Class::getSimpleName) // 获取类名 (不含包名)
            .collect(Collectors.joining(", ", "[", "]")); // 拼接：分隔符, 前缀, 后缀
        throw new _40047Exception500InvokerNull(returnType, parameters);
    }
}
