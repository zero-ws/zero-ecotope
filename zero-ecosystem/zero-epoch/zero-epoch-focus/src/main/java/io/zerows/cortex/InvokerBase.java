package io.zerows.cortex;

import io.r2mo.typed.exception.WebException;
import io.r2mo.vertx.function.FnVertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.zerows.cortex.exception._40018Exception500AsyncSignature;
import io.zerows.cortex.plugins.uddi.Uddi;
import io.zerows.cortex.plugins.uddi.UddiClient;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.constant.VValue;
import io.zerows.support.Ut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Uniform call TunnelClient to remove duplicated codes
 * Refactor invokder to support Dynamic Invoke
 */
@SuppressWarnings("all")
public abstract class InvokerBase implements Invoker {

    protected final Method method;

    protected InvokerBase(final Method method) {
        this.method = method;
    }

    protected Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Future method(JsonObject)
     * Future method(JsonArray)
     */
    protected Future invokeJson(
        final Object proxy,
        final Method method,
        final Envelop envelop) {
        // Preparing Method
        Invoker.ofPre(PreMe::new).execute(method, envelop);

        final Object reference = envelop.data();
        final Class<?> argType = method.getParameterTypes()[VValue.IDX];
        final Object arguments = Ut.deserialize(Ut.toString(reference), argType);
        return InvokerUtil.invoke(proxy, method, arguments);
    }

    protected <I> Envelop invokeWrap(final I input) {
        if (input instanceof Envelop) {
            // Return Envelop directly
            return (Envelop) input;
        } else {
            // Return Envelop building
            return Envelop.success(input);
        }
    }

    /**
     * R method(Tool..)
     */
    protected Object invokeInternal(final Object proxy, final Method method, final Envelop envelop) {
        // Preparing Method
        Invoker.ofPre(PreMe::new).execute(method, envelop);

        // Return value here.
        return InvokerUtil.invokeAsync(proxy, method, envelop, null);
    }

    protected void invokeInternal(final Object proxy, final Method method, final Message<Envelop> message) {
        final Envelop envelop = message.body();
        // Preparing Method
        Invoker.ofPre(PreMe::new).execute(method, envelop);

        // Return value here.
        InvokerUtil.invokeAsync(proxy, method, envelop, message);
    }


    protected <T> Handler<AsyncResult<T>> invokeHandler(final Message<Envelop> message) {
        return handler -> {
            if (handler.succeeded()) {
                message.reply(Envelop.moveOn(handler.result()));
            } else {
                // Readible codec for configured information, error flow needed.
                final WebException found = FnVertx.failAt(handler.cause());
                message.reply(Envelop.failure(found));
            }
        };
    }

    /**
     *
     */
    protected <I> Function<I, Future<Envelop>> nextEnvelop(
        final Vertx vertx,
        final Method method) {
        return item -> this.nextEnvelop(vertx, method, item);
    }

    protected <T> Future<Envelop> nextEnvelop(
        final Vertx vertx,
        final Method method,
        final T result
    ) {
        final UddiClient client = Uddi.client(getClass());
        return client.bind(vertx).bind(method).connect(Envelop.moveOn(result));
    }

    protected void failureAt(final boolean isOk, final Method method) {
        if (isOk) {
            return;
        }
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] paramCls = method.getParameterTypes();
        final String parameters = Arrays.stream(paramCls)
            .map(Class::getSimpleName) // 获取类名 (不含包名)
            .collect(Collectors.joining(", ", "[", "]")); // 拼接：分隔符, 前缀, 后缀
        throw new _40018Exception500AsyncSignature(returnType, parameters);
    }
}
