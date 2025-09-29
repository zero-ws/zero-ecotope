package io.zerows.core.web.invocation.uca.runner;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.core.web.invocation.exception._500ReturnNullException;
import io.zerows.core.web.model.commune.Envelop;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Future<Envelop> method(Envelop)
 */
@SuppressWarnings("all")
public class FutureInvoker extends AbstractInvoker {

    @Override
    public void ensure(final Class<?> returnType,
                       final Class<?> paramCls) {
        // Verify
        final boolean valid =
            Future.class.isAssignableFrom(returnType) && paramCls == Envelop.class;
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // Invoke directly
        final Envelop envelop = message.body();
        // Future<Tool>
        final Class<?> returnType = method.getReturnType();


        // LOG
        this.logger().info(
            INFO.MSG_DIRECT,
            this.getClass(),
            returnType,
            method.getName(),
            method.getDeclaringClass());


        // Get Tool
        final Class<?> tCls = returnType.getComponentType();
        if (Envelop.class == tCls) {
            final Future<Envelop> result = Ut.invoke(proxy, method.getName(), envelop);

            // Null Pointer return value checking
            FnZero.out(Objects.isNull(result), _500ReturnNullException.class, getClass(), method);

            result.onComplete(item -> message.reply(item.result()));
        } else {
            final Future tResult = Ut.invoke(proxy, method.getName(), envelop);

            // Null Pointer return value checking
            FnZero.out(Objects.isNull(tResult), _500ReturnNullException.class, getClass(), method);

            tResult.onComplete(invokeHandler(message));
        }
    }


    @Override
    public <I, O> void handle(final Object proxy, final Method method, final I input, final Handler<AsyncResult<O>> handler) {
        // Invoke directly
        final Envelop envelop = this.invokeWrap(input);
        // Future<Tool>
        final Class<?> returnType = method.getReturnType();


        // LOG
        this.logger().info(
            INFO.MSG_DIRECT,
            this.getClass(),
            returnType,
            method.getName(),
            method.getDeclaringClass());


        // Get Tool
        final Class<?> tCls = returnType.getComponentType();
        if (Envelop.class == tCls) {
            final Future<Envelop> result = Ut.invoke(proxy, method.getName(), envelop);

            // Null Pointer return value checking
            FnZero.out(Objects.isNull(result), _500ReturnNullException.class, getClass(), method);

            result.onComplete(item -> handler.handle(Future.succeededFuture((O) item.result())));
        } else {
            final Future result = Ut.invoke(proxy, method.getName(), envelop);

            // Null Pointer return value checking
            FnZero.out(Objects.isNull(result), _500ReturnNullException.class, getClass(), method);
            handler.handle(result);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void next(final Object proxy,
                     final Method method,
                     final Message<Envelop> message,
                     final Vertx vertx) {
        // Invoke directly
        final Envelop envelop = message.body();
        // Future<Tool>
        final Class<?> returnType = method.getReturnType();
        // Get Tool
        final Class<?> tCls = returnType.getComponentType();


        // LOG
        this.logger().info(
            INFO.MSG_RPC,
            this.getClass(),
            returnType,
            method.getName(),
            method.getDeclaringClass()
        );


        if (Envelop.class == tCls) {
            // Execute Future<Envelop>
            final Future<Envelop> future = InvokerUtil.invoke(proxy, method, envelop);

            future.compose(this.nextEnvelop(vertx, method))
                .onComplete(invokeHandler(message));
        } else {
            final Future future = InvokerUtil.invoke(proxy, method, envelop);

            future.compose(this.nextEnvelop(vertx, method))
                .onComplete(invokeHandler(message));
        }
    }
}
