package io.zerows.cortex;

import io.r2mo.SourceReflect;
import io.r2mo.typed.cc.Cc;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Replier for method invoking
 */
public interface Invoker {
    Cc<String, Invoker.Pre> CC_PRE = Cc.openThread();

    Cc<String, Invoker.Action> CC_ACTION = Cc.openThread();

    Cc<String, Invoker> CC_INVOKER = Cc.openThread();

    static Invoker of(final Class<?> invokerCls, final Method method) {
        final String keyCache = invokerCls.getName() + "@" + method.toString();
        return CC_INVOKER.pick(() -> SourceReflect.instance(invokerCls, method), keyCache);
    }

    static Invoker.Pre ofPre(final Supplier<Invoker.Pre> constructorFn) {
        return CC_PRE.pick(constructorFn, String.valueOf(constructorFn.hashCode()));
    }

    @SuppressWarnings("all")
    static Invoker.Action ofAction(final InvokerType type) {
        return switch (type) {
            case ONE_ENVELOP -> CC_ACTION.pick(CallDirect::new, type.name());
            case ONE_DYNAMIC -> CC_ACTION.pick(CallSingle::new, type.name());
            case STANDARD -> CC_ACTION.pick(CallDynamic::new, type.name());
        };
    }

    /**
     * Invoke method and replying
     *
     * @param proxy   Proxy object reference
     * @param method  Method reference for reflection
     * @param message Message handler
     */
    void invoke(Object proxy, Method method, Message<Envelop> message);

    /**
     * Invoke method and ( Ipc ) then replying
     *
     * @param proxy   Proxy object reference
     * @param method  Method reference for reflection
     * @param message Message handler
     * @param vertx   Vertx reference
     */
    void next(Object proxy, Method method, Message<Envelop> message, Vertx vertx);

    /**
     * Invoke method normalized, this api may be more useful
     *
     * @param proxy   Proxy object reference
     * @param method  Method reference for reflection
     * @param input   Envelop as input part
     * @param handler Async Handler to handle returned Tool
     * @param <I>     Input Type
     * @param <O>     Output Type
     */
    <I, O> void handle(Object proxy, Method method, I input, Handler<AsyncResult<O>> handler);

    interface Action {

        <T> T execute(Object proxy, Method method, Object... args);
    }

    interface Pre {

        void execute(Method method, Envelop envelop);
    }
}
