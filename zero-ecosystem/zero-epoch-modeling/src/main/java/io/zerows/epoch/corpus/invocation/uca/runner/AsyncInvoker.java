package io.zerows.epoch.corpus.invocation.uca.runner;

import io.r2mo.function.Fn;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.model.commune.Envelop;
import io.zerows.epoch.corpus.mature.exception._60051Exception500ReturnNull;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Future<Tool> method(I)
 */
public class AsyncInvoker extends AbstractInvoker {

    @Override
    public void ensure(final Class<?> returnType, final Class<?> paramCls) {
        // Verify
        final boolean valid =
            (void.class != returnType && Void.class != returnType);
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    @SuppressWarnings("all")
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        final Envelop envelop = message.body();
        // Deserialization from message bus.
        final Class<?> returnType = method.getReturnType();
        // LOG
        this.logger().info(
            INFO.MSG_DIRECT,
            this.getClass(),
            returnType,
            method.getName(),
            method.getDeclaringClass()
        );


        // Get Tool
        final Class<?> tCls = returnType.getComponentType();
        if (Envelop.class == tCls) {
            // Input type is Envelop, input directly
            final Future<Envelop> result = Ut.invoke(proxy, method.getName(), envelop);

            // Null Pointer return value checking
            Fn.jvmKo(Objects.isNull(result), _60051Exception500ReturnNull.class, method);

            result.onComplete(item -> message.reply(item.result()));
            // result.setHandler(item -> message.reply(item.result()));
        } else {
            final Object returnValue = this.invokeInternal(proxy, method, envelop);
            // Null Pointer return value checking
            // FnZero.out(Objects.isNull(returnValue), _500ReturnNullException.class, getClass(), method);
            if (null == returnValue) {
                final Promise promise = Promise.promise();
                promise.future().onComplete(invokeHandler(message));
            } else {

                final Future future = (Future) returnValue;
                future.onComplete(invokeHandler(message));
            }
        }
    }

    @Override
    @SuppressWarnings("all")
    public <I, O> void handle(final Object proxy, final Method method, final I input, final io.vertx.core.Handler<AsyncResult<O>> handler) {
        final Envelop envelop = this.invokeWrap(input);

        // Deserialization from message bus.
        final Class<?> returnType = method.getReturnType();


        // LOG
        this.logger().info(
            INFO.MSG_HANDLE,
            this.getClass(),
            returnType,
            method.getName(),
            method.getDeclaringClass()
        );


        // Get Tool
        final Class<?> tCls = returnType.getComponentType();
        if (Envelop.class == tCls) {
            // Input type is Envelop, input directly
            final Future<Envelop> result = Ut.invoke(proxy, method.getName(), envelop);

            // Null Pointer return value checking
            Fn.jvmKo(Objects.isNull(result), _60051Exception500ReturnNull.class, method);

            result.onComplete(item -> handler.handle(Future.succeededFuture((O) item.result())));
            // result.setHandler(item -> message.reply(item.result()));
        } else {
            final Object returnValue = this.invokeInternal(proxy, method, envelop);
            // Null Pointer return value checking
            // FnZero.out(Objects.isNull(returnValue), _500ReturnNullException.class, getClass(), method);
            if (null == returnValue) {
                handler.handle(Future.succeededFuture());
            } else {
                final Future future = (Future) returnValue;
                future.onComplete(item -> handler.handle(Future.succeededFuture((O) ((AsyncResult<O>) item).result())));
                //                handler.handle(future);
            }
        }
    }

    @Override
    @SuppressWarnings("all")
    public void next(final Object proxy,
                     final Method method,
                     final Message<Envelop> message,
                     final Vertx vertx) {
        final Envelop envelop = message.body();
        // Deserialization from message bus.
        final Class<?> returnType = method.getReturnType();


        // LOG
        this.logger().info(
            INFO.MSG_RPC,
            this.getClass(),
            returnType,
            method.getName(),
            method.getDeclaringClass()
        );


        // Get Tool
        final Class<?> tCls = returnType.getComponentType();
        if (Envelop.class == tCls) {
            // Input type is Envelop, input directly
            final Future<Envelop> result = Ut.invoke(proxy, method.getName(), envelop);
            result.compose(this.nextEnvelop(vertx, method))
                .onComplete(invokeHandler(message));
        } else {
            final Future future = this.invokeJson(proxy, method, envelop);
            future.compose(this.nextEnvelop(vertx, method))
                .onComplete(invokeHandler(message));
        }
    }
}
