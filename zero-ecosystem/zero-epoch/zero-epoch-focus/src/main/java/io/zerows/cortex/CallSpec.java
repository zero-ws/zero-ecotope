package io.zerows.cortex;

import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CallSpec {
    public static boolean isRetVoid(final Method method) {
        final Class<?> returnType = method.getReturnType();
        return void.class == returnType || Void.class == returnType;
    }

    public static boolean isRetEnvelop(final Method method) {
        final Class<?> returnType = method.getReturnType();
        return Envelop.class == returnType;
    }

    public static boolean isRetFuture(final Method method) {
        final Class<?> returnType = method.getReturnType();
        return Future.class.isAssignableFrom(returnType);
    }

    public static boolean isInEnvelop(final Method method) {
        final Class<?>[] paramCls = method.getParameterTypes();
        return 1 == paramCls.length && Envelop.class == paramCls[0];
    }

    public static boolean isInMessage(final Method method) {
        final Class<?>[] paramCls = method.getParameterTypes();
        return Arrays.stream(paramCls).anyMatch(Message.class::isAssignableFrom);
    }

}
