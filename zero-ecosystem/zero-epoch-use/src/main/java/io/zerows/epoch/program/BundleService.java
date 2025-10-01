package io.zerows.epoch.program;

import io.r2mo.function.Fn;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2024-06-26
 */
class BundleService {

    static <T> T service(final Class<T> interfaceCls, final Bundle owner) {
        Objects.requireNonNull(owner);
        final BundleContext context = owner.getBundleContext();
        final ServiceReference<T> reference = context.getServiceReference(interfaceCls);
        if (Objects.isNull(reference)) {
            return null;
        }
        return context.getService(reference);
    }

    static <T> List<T> serviceList(final Class<T> interfaceCls, final Bundle owner) {
        Objects.requireNonNull(owner);
        final BundleContext context = owner.getBundleContext();
        final Collection<ServiceReference<T>> references = Fn.jvmOr(() -> context.getServiceReferences(interfaceCls, null));
        return references.stream().filter(Objects::nonNull)
            .map(context::getService)
            .toList();
    }
}
