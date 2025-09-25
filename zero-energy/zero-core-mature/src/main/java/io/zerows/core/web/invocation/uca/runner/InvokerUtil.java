package io.zerows.core.web.invocation.uca.runner;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Session;
import io.zerows.ams.constant.VValue;
import io.zerows.core.fn.Fx;
import io.zerows.core.util.Ut;
import io.zerows.core.web.invocation.exception.BootAsyncSignatureException;
import io.zerows.core.web.invocation.exception.BootWorkerArgumentException;
import io.zerows.core.web.invocation.uca.parameter.ParameterBuilder;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.module.domain.uca.serialization.ZeroType;
import io.zerows.module.metadata.uca.logging.OLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Tool for invoker do shared works.
 */
@SuppressWarnings("all")
public class InvokerUtil {
    private static final OLog LOGGER = Ut.Log.uca(InvokerUtil.class);

    public static Object invokeCall(
        final Object proxy,
        final Method method,
        final Envelop envelop
    ) {
        Object returnValue;
        final Class<?>[] argTypes = method.getParameterTypes();
        final Class<?> returnType = method.getReturnType();
        if (VValue.ONE == method.getParameterCount()) {
            final Class<?> firstArg = argTypes[VValue.IDX];
            if (Envelop.class == firstArg) {
                // Input type is Envelop, input directly
                returnValue = InvokerUtil.invoke(proxy, method, envelop);
                // Ut.invoke(proxy, method.getName(), envelop);
            } else {
                // One type dynamic here
                returnValue = InvokerUtil.invokeSingle(proxy, method, envelop);
            }
        } else {
            // Multi parameter dynamic here
            returnValue = InvokerUtil.invokeMulti(proxy, method, envelop);
        }
        return returnValue;
    }

    public static <T> T invoke(final Object proxy, final Method method, final Object... args) {
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

    /**
     * Whether this method is void
     *
     * @param method checked method
     *
     * @return checked result
     */
    static boolean isVoid(final Method method) {
        final Class<?> returnType = method.getReturnType();
        return void.class == returnType || Void.class == returnType;
    }

    /**
     * TypedArgument verification
     * Public for replacing duplicated code
     *
     * @param method checked method.
     * @param target checked class
     */
    public static void verifyArgs(final Method method,
                                  final Class<?> target) {

        // 1. Ensure method length
        final Class<?>[] params = method.getParameterTypes();
        final OLog logger = Ut.Log.uca(target);
        // 2. The parameters
        Fx.outBoot(VValue.ZERO == params.length,
            logger, BootWorkerArgumentException.class,
            target, method);
    }

    static void verify(
        final boolean condition,
        final Class<?> returnType,
        final Class<?> paramType,
        final Class<?> target) {
        final OLog logger = Ut.Log.uca(target);
        Fx.outBoot(condition, logger,
            BootAsyncSignatureException.class, target,
            returnType.getName(), paramType.getName());
    }

    private static Object getValue(final Class<?> type,
                                   final Envelop envelop,
                                   final Supplier<Object> defaultSupplier) {
        // Multi calling for Session type
        final Object value;
        if (Session.class == type) {
            /*
             * RBAC required ( When Authenticate )
             * 1) Provide username / password to get data from remote server.
             * 2) Request temp authorization code ( Required Session ).
             */
            value = envelop.session();
        } else {
            value = defaultSupplier.get();
            final Object argument = null == value ? null : ZeroType.value(type, value.toString());
        }
        return value;
    }

    static Object invokeMulti(final Object proxy,
                              final Method method,
                              final Envelop envelop) {
        /*
         * One type dynamic here
         */
        final Object reference = envelop.data();
        /*
         * Non Direct
         */
        final Object[] arguments = new Object[method.getParameterCount()];
        final JsonObject json = (JsonObject) reference;
        final Class<?>[] types = method.getParameterTypes();
        /*
         * Adjust argument index
         */
        int adjust = 0;
        for (int idx = 0; idx < types.length; idx++) {
            /*
             * Multi calling for Session type
             */
            final Class<?> type = types[idx];
            /*
             * Found typed here
             * Adjust idx  - 1 to move argument index to
             * left.
             * {
             *    "0": "key",
             *    "1": "type",
             * }
             * (String,<Tool>,String) -> (idx, current), (0, 0), (1, ?), (2, 1)
             *                                               adjust = 1
             *
             * (<Tool>, String, String) -> (idx, current), (0, ?), (1, 0), (2, 1)
             *                                          adjust = 1
             *
             * (String, String,<Tool>) -> (idx, current), (0, 0), (1, 1), (2, ?)
             *                                                          adjust = 1
             */
            // Old: TypedArgument.analyzeWorker
            final ParameterBuilder<Envelop> builder = ParameterBuilder.ofWorker();
            final Object analyzed = builder.build(envelop, type);
            if (Objects.isNull(analyzed)) {
                final int current = idx - adjust;
                final Object value = json.getValue(String.valueOf(current));
                if (Objects.isNull(value)) {
                    /*
                     * Input is null when type is not match, if type is JsonObject
                     * The result should be json instead of `null`
                     */
                    if (JsonObject.class == type && VValue.IDX == idx) {
                        /*
                         * Here are often the method as
                         * method(JsonObject, ...) format
                         */
                        arguments[idx] = json.copy();
                    } else {
                        arguments[idx] = null;
                    }
                } else {
                    /*
                     * Serialization
                     */
                    arguments[idx] = ZeroType.value(type, value.toString());
                }
            } else {
                /*
                 * EmType successfully
                 */
                arguments[idx] = analyzed;
                adjust += 1;
            }
        }
        return invoke(proxy, method, arguments);
        // return Ut.invoke(proxy, method.getName(), arguments);
    }

    static Object invokeSingle(final Object proxy,
                               final Method method,
                               final Envelop envelop) {
        final Class<?> argType = method.getParameterTypes()[VValue.IDX];
        // Append single argument
        final ParameterBuilder<Envelop> builder = ParameterBuilder.ofWorker();
        final Object analyzed = builder.build(envelop, argType);
        if (Objects.isNull(analyzed)) {
            // One type dynamic here
            final Object reference = envelop.data();
            // Non Direct
            Object parameters = reference;
            if (JsonObject.class == reference.getClass()) {
                final JsonObject json = (JsonObject) reference;
                if (modeInterface(json)) {
                    // Proxy mode
                    if (VValue.ONE == json.fieldNames().size()) {
                        // New Mode for direct type
                        parameters = json.getValue("0");
                    }
                }
            }
            final Object arguments = ZeroType.value(argType, Ut.toString(parameters));
            return invoke(proxy, method, arguments); // Ut.invoke(proxy, method.getName(), arguments);
        } else {
            /*
             * XHeader
             * User
             * Session
             * These three argument types could be single
             */
            return invoke(proxy, method, analyzed); // Ut.invoke(proxy, method.getName(), analyzed);
        }
    }

    private static boolean modeInterface(final JsonObject json) {
        final long count = json.fieldNames().stream().filter(Ut::isInteger)
            .count();
        // All json keys are numbers
        LOGGER.debug("( isInterface Mode ) get count: {0}, json: {1}",
            count, json.encode());
        return count == json.fieldNames().size();
    }
}
