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
 * void method(Messsage<Envelop>)
 */
public class InvokerMessage extends InvokerBase {
    private InvokerMessage(final Method method) {
        super(method);
        final boolean isOk = CallSpec.isRetVoid(method)
            && CallSpec.isInMessage(method);
        // 合法：返回值必须是 void / Void 且参数必须包含 Message
        this.failureAt(isOk, method);
    }

    @Override
    public void invoke(final Object proxy,
                       final Method method,
                       final Message<Envelop> message) {
        // Invoker and do not reply
        this.invokeInternal(proxy, method, message);
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
