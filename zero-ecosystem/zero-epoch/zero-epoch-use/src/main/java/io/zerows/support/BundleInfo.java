package io.zerows.support;

import io.zerows.platform.constant.VString;
import io.zerows.epoch.constant.KName;
import org.apache.felix.dm.Component;
import org.apache.felix.dm.ServiceDependency;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author lang : 2024-04-28
 */
class BundleInfo {
    static String keyCache(final Bundle bundle, final boolean version) {
        if (version) {
            return bundle.getSymbolicName() + VString.SLASH + bundle.getVersion().getQualifier();
        } else {
            return bundle.getSymbolicName();
        }
    }


    static String keyCache(final Bundle bundle, final Class<?> clazz) {
        if (Objects.isNull(bundle)) {
            return clazz.getName();
        } else {
            return keyCache(bundle, false);
        }
    }

    static Component addDependency(final Component callback,
                                   final Supplier<ServiceDependency> serviceSupplier,
                                   final Class<?>... serviceClsArr) {
        for (final Class<?> serviceCls : serviceClsArr) {
            callback.add(serviceSupplier.get().setService(serviceCls)
                .setRequired(Boolean.TRUE).setCallbacks(KName.START, KName.STOP));
        }
        return callback;
    }
}
