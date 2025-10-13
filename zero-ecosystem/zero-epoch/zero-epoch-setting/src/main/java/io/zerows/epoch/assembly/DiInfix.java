package io.zerows.epoch.assembly;

import io.zerows.component.log.LogO;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2023-06-02
 */
class DiInfix {

    public static final String IMPL_NULL = "The system scanned null infix for key = {0} " +
        "on the field \"{1}\" of {2}";
    public static final String IMPL_WRONG = "The hitted class {0} does not implement the interface" +
        "of {1}";
    private transient final LogO logger;

    DiInfix(final Class<?> clazz) {
        this.logger = Ut.Log.metadata(clazz);
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
        return proxy;
    }
}
