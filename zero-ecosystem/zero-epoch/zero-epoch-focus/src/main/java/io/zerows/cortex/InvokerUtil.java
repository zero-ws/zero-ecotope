package io.zerows.cortex;

import io.vertx.ext.web.Session;
import io.zerows.epoch.web.Envelop;
import io.zerows.weaver.ZeroType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * Tool for invoker do shared works.
 */
@SuppressWarnings("all")
@Slf4j
public class InvokerUtil {
    public static final String MSG_DIRECT = "( Call ) Invoker = {}, ReturnType = {}, Method = {}, Class = {}.";
    public static final String MSG_RPC = "( Call Rpc ) Invoker = {}, ReturnType = {}, Method = {}, Class = {}.";
    public static final String MSG_HANDLE = "( Call Handle ) Invoker = {}, ReturnType = {}, Method = {}, Class = {}.";

    public static Object invokeWorker(final Object proxy,
                                      final Method method,
                                      final Envelop envelop
    ) {
        // CallDirect
        if (CallSpec.isInEnvelop(method)) {
            return Invoker.ofAction(InvokerType.ONE_ENVELOP);
        }

        if (1 == method.getParameterCount()) {
            return Invoker.ofAction(InvokerType.ONE_DYNAMIC);
        }


        return Invoker.ofAction(InvokerType.STANDARD);
    }

    public static <T> T invoke(final Object proxy,
                               final Method method,
                               final Object... args) {
        return Invoker.ofAction(InvokerType.ONE_ENVELOP).execute(proxy, method, args);
    }

    private static Object getValue(final Class<?> type,
                                   final Envelop envelop,
                                   final Supplier<Object> defaultSupplier) {
        // Multi calling for Session type
        final Object value;
        if (Session.class == type) {
            /*
             * RBAC required ( When Authenticate )
             * 1) Provide username / password to findRunning data from remote server.
             * 2) Request temp authorization code ( Required Session ).
             */
            value = envelop.session();
        } else {
            value = defaultSupplier.get();
            final Object argument = null == value ? null : ZeroType.value(type, value.toString());
        }
        return value;
    }
}
