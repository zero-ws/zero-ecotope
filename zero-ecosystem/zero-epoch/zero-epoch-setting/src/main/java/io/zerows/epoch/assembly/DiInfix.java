package io.zerows.epoch.assembly;

import io.r2mo.function.Fn;
import io.reactivex.rxjava3.core.Observable;
import io.zerows.component.log.OLog;
import io.zerows.epoch.annotations.Infusion;
import io.zerows.management.OCacheClass;
import io.zerows.management.OZeroStore;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.sdk.plugins.Infix;
import io.zerows.support.Ut;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023-06-02
 */
class DiInfix {

    public static final String IMPL_NULL = "The system scanned null infix for key = {0} " +
        "on the field \"{1}\" of {2}";
    public static final String IMPL_WRONG = "The hitted class {0} does not implement the interface" +
        "of {1}";
    private static final ConcurrentMap<Class<?>, Class<?>> INFUSION = infusionMap();
    private transient final OLog logger;

    DiInfix(final Class<?> clazz) {
        this.logger = Ut.Log.metadata(clazz);
    }

    private static ConcurrentMap<Class<?>, Class<?>> infusionMap() {
        // Extract all infixes
        final Set<Class<?>> infixes = new HashSet<>(OZeroStore.classInject().values());
        final ConcurrentMap<Class<?>, Class<?>> binds = new ConcurrentHashMap<>();
        Observable.fromIterable(infixes)
            .filter(Infix.class::isAssignableFrom)
            .subscribe(item -> {
                final Method method = Fn.jvmOr(() -> item.getDeclaredMethod("get"));
                final Class<?> type = method.getReturnType();
                binds.put(type, item);
            })
            .dispose();
        return binds;
    }

    Object wrapInfix(final Object proxy) {
        if (Objects.isNull(proxy)) {
            return null;
        }
        final Class<?> typeOf = proxy.getClass();

        final Set<Class<?>> classTps = OCacheClass.entireValue(VertxComponent.INFUSION);
        if (!classTps.contains(typeOf)) {
            return proxy;
        }
        final Class<?> type = proxy.getClass();
        Observable.fromArray(type.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(Infusion.class))
            .subscribe(field -> {
                final Class<?> fieldType = field.getType();
                final Class<?> infixCls = INFUSION.get(fieldType);
                if (null != infixCls) {
                    if (Ut.isImplement(infixCls, Infix.class)) {
                        final Infix reference = Ut.singleton(infixCls);
                        final Object tpRef = Ut.invoke(reference, "get");
                        final String fieldName = field.getName();
                        Ut.field(proxy, fieldName, tpRef);
                    } else {
                        this.logger.warn(IMPL_WRONG, infixCls.getName(), Infix.class.getName());
                    }
                } else {
                    this.logger.warn(IMPL_NULL, field.getType().getName(), field.getName(), type.getName());
                }
            })
            .dispose();
        return proxy;
    }
}
