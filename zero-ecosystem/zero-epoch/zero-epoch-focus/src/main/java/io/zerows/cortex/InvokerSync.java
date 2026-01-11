package io.zerows.cortex;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;

/**
 * Envelop method(Envelop)
 */
public class InvokerSync extends InvokerBase {

    private InvokerSync(final Method method) {
        super(method);
        final boolean isOk = CallSpec.isRetEnvelop(method)
            && CallSpec.isInEnvelop(method);
        // 合法：返回值必须是 Envelop 且参数必须是单个 Envelop
        this.failureAt(isOk, method);
    }

    @Override
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // LOG
        this.logger().info(
            InvokerUtil.MSG_DIRECT,
            this.getClass(),
            method.getReturnType(),
            method.getName(),
            method.getDeclaringClass()
        );

        // Invoke directly
        final Envelop envelop = message.body();
        message.reply(InvokerUtil.invoke(proxy, method, envelop));
    }

    @Override
    public void next(final Object proxy,
                     final Method method,
                     final Message<Envelop> message,
                     final Vertx vertx) {
        // LOG
        this.logger().info(
            InvokerUtil.MSG_RPC,
            this.getClass(),
            method.getReturnType(),
            method.getName(),
            method.getDeclaringClass()
        );

        final Envelop envelop = message.body();
        final Envelop result = InvokerUtil.invoke(proxy, method, envelop);
        this.nextEnvelop(vertx, method, result)
            .onComplete(this.invokeHandler(message));
    }

    @Override
    public <I, O> void handle(final Object proxy, final Method method,
                              final I input, final Handler<AsyncResult<O>> handler) {
        // LOG
        this.logger().info(
            InvokerUtil.MSG_HANDLE,
            this.getClass(),
            method.getReturnType(),
            method.getName(),
            method.getDeclaringClass()
        );

        final Envelop envelop = this.invokeWrap(input);
        final Envelop result = InvokerUtil.invoke(proxy, method, envelop);
        final O extracted = result.data();
        handler.handle(Future.succeededFuture(extracted));
    }
}
