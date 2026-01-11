package io.zerows.cortex;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.cortex.exception._60027Exception501RpcReject;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.exception._60050Exception501NotSupport;

import java.lang.reflect.Method;

/**
 * void method(Envelop)
 */
public class InvokerPing extends InvokerBase {

    @Override
    public void canInvoke(final Class<?> returnType,
                          final Class<?>[] paramCls) {
        final boolean valid = (void.class == returnType || Void.class == returnType)
            && 1 == paramCls.length && paramCls[0] == Envelop.class;
        this.canInvoke(!valid, returnType, paramCls);
    }

    @Override
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // Invoke directly
        final Envelop envelop = message.body();
        InvokerUtil.invoke(proxy, method, envelop); // Ut.invoke(proxy, method.getName(), envelop);
        message.reply(Envelop.success(Boolean.TRUE));
    }

    @Override
    public void next(final Object proxy,
                     final Method method,
                     final Message<Envelop> message,
                     final Vertx vertx) {
        // Return void is reject by Rpc continue
        throw new _60027Exception501RpcReject();
    }

    @Override
    public <I, O> void handle(final Object proxy, final Method method,
                              final I input, final Handler<AsyncResult<O>> handler) {
        // Return void is reject by Standard Invoke
        throw new _60050Exception501NotSupport(this.getClass());
    }
}
