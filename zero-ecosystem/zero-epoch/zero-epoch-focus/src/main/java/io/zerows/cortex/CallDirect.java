package io.zerows.cortex;

import io.zerows.support.Ut;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallDirect implements Invoker.Action {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(final Object proxy, final Method method, final Object... args) {
        /*
         * Be sure to trust args first calling and then normalized calling
         * by `Ut.invoke`, because `Ut.invoke` will parse many parameters here, it means that
         * it will analyze the metadata information in running, I think it's not needed in
         * zero framework now. the method could be invoked with args directly.
         */
        try {
            return (T) method.invoke(proxy, args);
        } catch (final InvocationTargetException | IllegalAccessException ex) {
            return Ut.invoke(proxy, method.getName(), args);
        }
    }
}
