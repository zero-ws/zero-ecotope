package io.zerows.cortex;

import io.zerows.epoch.web.Envelop;

import java.lang.reflect.Method;

class CallDynamic implements Invoker.Action {
    @Override
    public <T> T execute(final Object proxy, final Method method, final Object... args) {
        final Envelop envelop = (Envelop) args[0];
        final Object[] arguments = CallUtil.parseArguments(method, envelop);
        return Invoker.ofAction(InvokerType.ONE_ENVELOP).execute(proxy, method, arguments);
    }
}
