package io.zerows.cortex;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;

public class InvokerDim extends InvokerBase {

    private InvokerDim(final Method method) {
        super(method);
        final boolean isOk = !CallSpec.isRetVoid(method);
        // 合法：返回值不能是 void / Void
        this.failureAt(isOk, method);
    }

    @Override
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // Log
        this.logger().info(
            InvokerUtil.MSG_DIRECT,
            this.getClass(),
            method.getReturnType(),
            method.getName(),
            method.getDeclaringClass()
        );

        final Envelop envelop = message.body();
        final Object returnValue = this.invokeInternal(proxy, method, envelop);
        // The returnValue type could not be Future
        message.reply(returnValue);
    }

    @Override
    public void next(final Object proxy,
                     final Method method,
                     final Message<Envelop> message,
                     final Vertx vertx) {
        // Log
        this.logger().info(
            InvokerUtil.MSG_RPC,
            this.getClass(),
            method.getReturnType(),
            method.getName(),
            method.getDeclaringClass()
        );

        final Envelop envelop = message.body();
        final Object returnValue = this.invokeInternal(proxy, method, envelop);
        this.nextEnvelop(vertx, method, returnValue)
            .onComplete(this.invokeHandler(message));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I, O> void handle(final Object proxy, final Method method,
                              final I input, final Handler<AsyncResult<O>> handler) {
        // Log
        this.logger().info(
            InvokerUtil.MSG_HANDLE,
            this.getClass(),
            method.getReturnType(),
            method.getName(),
            method.getDeclaringClass()
        );

        // 「Sync」
        final Envelop normalized = this.invokeWrap(input);
        final Object returnValue = this.invokeInternal(proxy, method, normalized);
        handler.handle(Future.succeededFuture((O) returnValue));
    }
}
