package io.zerows.epoch.corpus.assembly;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class OInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return method.invoke(proxy, args);
    }
}
