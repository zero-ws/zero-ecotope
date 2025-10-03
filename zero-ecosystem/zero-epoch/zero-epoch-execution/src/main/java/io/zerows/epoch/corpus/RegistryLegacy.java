package io.zerows.epoch.corpus;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.base.FnBase;
import io.zerows.epoch.application.YmlCore;
import io.zerows.epoch.database.jooq.JooqInfix;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * @author lang : 2023-06-09
 */
class RegistryLegacy {

    @SuppressWarnings("all")
    static Future<Boolean> registryMod(final Vertx vertx, final JsonObject initConfig) {
        /* Extract Component Class and consider running at the same time */
        // 1. nativeComponents first
        final JsonArray components = Ut.valueJArray(initConfig, YmlCore.init.CONFIGURE);
        return registryComponent(components, vertx).compose(nil -> {
            // 2. nativeBridge first
            final JsonArray bridges = Ut.valueJArray(initConfig, YmlCore.init.COMPILE);
            if (0 < bridges.size()) {
                // BootJooqConfigurationException
                JooqInfix.init(vertx);
                return registryBridge(bridges, vertx);
            } else {
                return Ux.futureT();
            }
        });
    }

    private static Future<Boolean> registryBridge(final JsonArray bridges, final Vertx vertx) {
        final List<JsonObject> ordered = bridges.stream()
            .filter(json -> json instanceof JsonObject)
            .map(json -> (JsonObject) json)
            // .sorted(Comparator.comparingInt(left -> left.getInteger(KName.ORDER, 0)))
            .toList();
        final Set<Function<Boolean, Future<Boolean>>> queue = new HashSet<>();
        // passion action（按顺序执行bridge组件）
        ordered.forEach(params -> {
            final Class<?> componentCls = Ut.valueC(params, YmlCore.init.compile.COMPONENT);
            if (Objects.nonNull(componentCls)) {
                final Set<Method> methodSet = registryBridgeMethod(componentCls);
                methodSet.forEach(method -> queue.add(json -> invokeComponent(componentCls, method, vertx)));
            }
        });
        return FnBase.parallel(Boolean.TRUE, queue);
    }

    private static Set<Method> registryBridgeMethod(final Class<?> clazz) {
        final Method[] methods = clazz.getDeclaredMethods();
        final Set<Method> methodSet = new HashSet<>();
        for (final Method method : methods) {
            /*
             * 1. name must start with `init`
             * 2. parameters definition must be 0 or 1
             * 3. 1 -> Return type must be Future<>
             *    0 -> Return type must be void
             */
            final int modifier = method.getModifiers();
            if (!Modifier.isPublic(modifier)) {
                continue;
            }
            if (!Modifier.isStatic(modifier)) {
                continue;
            }
            final String name = method.getName();
            if (!name.startsWith(YmlCore.init.__KEY)) {
                continue;
            }
            final int counter = method.getParameterTypes().length;
            if (1 < counter) {
                continue;
            }
            if (1 == counter) {
                final Class<?> type = method.getParameterTypes()[0];
                if (Vertx.class != type) {
                    continue;
                }
            }
            methodSet.add(method);
        }
        return methodSet;
    }

    @SuppressWarnings("all")
    private static Future<Boolean> registryComponent(final JsonArray components, final Vertx vertx) {
        /* Extract Component Class and consider running at the same time */
        final List<Future<Boolean>> async = new ArrayList<>();
        Ut.itJArray(components).forEach(json -> {
            final String className = json.getString(YmlCore.init.configure.COMPONENT);
            final Class<?> clazz = Ut.clazz(className, null);
            if (Objects.nonNull(clazz)) {
                /*
                 * Re-Calc the workflow by `init` method
                 * 1. init(Vertx) first
                 * 2. init() Secondary
                 */
                final Future<Boolean> ret = invokeComponent(clazz, YmlCore.init.__KEY, vertx);
                if (Objects.nonNull(ret)) {
                    async.add(ret);
                }
            }
        });
        return FnBase.combineB(async);
    }

    private static Future<Boolean> invokeComponent(final Class<?> clazz, final String methodName, final Vertx vertx) {
        Objects.requireNonNull(clazz);
        /*
         * Re-Calc the workflow by `init` method
         * 1. init(Vertx) first
         * 2. init() Secondary
         */
        final Method[] methods = clazz.getDeclaredMethods();
        final Method methodInit = Arrays.stream(methods)
            .filter(method -> methodName.equals(method.getName()))
            .findFirst().orElse(null);
        return invokeComponent(clazz, methodInit, vertx);
    }

    @SuppressWarnings("unchecked")
    private static Future<Boolean> invokeComponent(final Class<?> clazz, final Method methodInit, final Vertx vertx) {
        Objects.requireNonNull(clazz);
        if (Objects.nonNull(methodInit)) {
            final int counter = methodInit.getParameterTypes().length;
            final boolean isAsync = 0 < counter;
            if (isAsync) {
                // Async:  Future<Boolean> init(Vertx vertx) | init()
                return Fn.jvmOr(() -> (Future<Boolean>) methodInit.invoke(null, vertx));
            } else {
                // Sync:   void init(Vertx vertx) | init()
                return Fn.jvmOr(() -> {
                    methodInit.invoke(null);
                    return Future.succeededFuture(Boolean.TRUE);
                });
            }
        } else {
            // Empty Body ( Not invoking happened )
            return Ux.futureT();
        }
    }
}
