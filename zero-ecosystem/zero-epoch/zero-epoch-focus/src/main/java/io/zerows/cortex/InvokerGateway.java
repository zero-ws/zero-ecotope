package io.zerows.cortex;

import io.r2mo.function.Fn;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zerows.cortex.exception._40047Exception500InvokerNull;
import io.zerows.epoch.web.Envelop;

import java.util.Objects;

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

    public static Invoker invoker(final Class<?> returnType,
                                  final Class<?> paramCls) {
        Invoker invoker = null;
        if (void.class == returnType || Void.class == returnType) {

            /*
             * 「Async Support」
             * Method return type is: void/Void
             * It means that you must implement internal async operation in
             * the method programming.
             */
            if (Envelop.class == paramCls) {
                // void method(Envelop)
                invoker = CCT_INVOKER.pick(InvokerPing::new, InvokerPing.class.getName()); // Ut.?ingleton(PingInvoker.class);
            } else if (Message.class.isAssignableFrom(paramCls)) {
                // void method(Message<Envelop>)
                invoker = CCT_INVOKER.pick(InvokerMessage::new, InvokerMessage.class.getName()); // Ut.?ingleton(MessageInvoker.class);
            } else {
                // void method(Tool)
                invoker = CCT_INVOKER.pick(InvokerPingT::new, InvokerPingT.class.getName()); // Ut.?ingleton(PingTInvoker.class);
            }
        } else if (Envelop.class == returnType) {


            /*
             * 「Sync Only」
             * Method return type is: Envelop
             * This operation of method is sync operation definition, you can not
             * do any async operation in this kind of mode
             */
            if (Envelop.class == paramCls) {
                // Envelop method(Envelop)
                // Rpc supported.
                invoker = CCT_INVOKER.pick(InvokerSync::new, InvokerSync.class.getName()); // Ut.?ingleton(SyncInvoker.class);
            } else {
                // Envelop method(I)
                invoker = CCT_INVOKER.pick(InvokerDim::new, InvokerDim.class.getName()); // Ut.?ingleton(DimInvoker.class);
            }
        } else if (Future.class.isAssignableFrom(returnType)) {


            /*
             * 「Async Only」
             * Method return type is: Future
             * This operation of method is async operation definition, you can not
             * do any sync operation in this kind of mode
             */
            if (Envelop.class == paramCls) {
                // Future<Tool> method(Envelop)
                // Rpc supported.
                invoker = CCT_INVOKER.pick(InvokerFuture::new, InvokerFuture.class.getName()); // Ut.?ingleton(FutureInvoker.class);
            } else {
                // Future<Tool> method(I)
                // Rpc supported.
                invoker = CCT_INVOKER.pick(InvokerAsync::new, InvokerAsync.class.getName()); // Ut.?ingleton(AsyncInvoker.class);
            }
        } else {


            /*
             * 「Freedom」
             * Freedom mode is standard java specification method here, but in this kind of
             * mode, the framework remove three situations:
             * 1. Return = void/Void
             * 2. Return = Envelop
             * 3. Return = Future
             *
             * Also when you process this kind of method definition, the `Message` could not
             * be used, if you want to do Async operation, you can select `Future` returned type
             * as code major style in zero framework, it's recommend
             */
            if (!Message.class.isAssignableFrom(paramCls)) {
                // Java direct type, except Message<Tool> / Envelop
                // Tool method(I)
                // Rpc supported.
                invoker = CCT_INVOKER.pick(InvokerDynamic::new, InvokerDynamic.class.getName()); // Ut.?ingleton(DynamicInvoker.class);
            }
        }
        Fn.jvmKo(Objects.isNull(invoker), _40047Exception500InvokerNull.class, returnType, paramCls);
        return invoker;
    }
}
