package io.zerows.core.web.invocation.uca.runner;

import io.zerows.core.exception.web._501NotSupportException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.core.web.invocation.exception._501RpcRejectException;
import io.zerows.core.web.model.commune.Envelop;

import java.lang.reflect.Method;

/**
 * void method(Messsage<Envelop>)
 */
public class MessageInvoker extends AbstractInvoker {
    @Override
    public void ensure(final Class<?> returnType,
                       final Class<?> paramCls) {
        // Verify
        final boolean valid =
            (void.class == returnType || Void.class == returnType)
                && Message.class.isAssignableFrom(paramCls);
        InvokerUtil.verify(!valid, returnType, paramCls, this.getClass());
    }

    @Override
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // Invoker and do not reply
        InvokerUtil.invoke(proxy, method, message);
        // Ut.invoke(proxy, method.getName(), message);
    }

    @Override
    public void next(final Object proxy,
                     final Method method,
                     final Message<Envelop> message,
                     final Vertx vertx) {
        // Return void is reject by Rpc continue
        throw new _501RpcRejectException(this.getClass());
    }

    @Override
    public <I, O> void handle(final Object proxy, final Method method,
                              final I input, final Handler<AsyncResult<O>> handler) {
        // Return void is reject by Standard Invoke
        throw new _501NotSupportException(this.getClass());
    }
}
