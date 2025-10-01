package io.zerows.epoch.corpus.metadata.store.service;

import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-07-01
 */
class ServiceForContext {

    private static final ConcurrentMap<Long, ServiceContext> CONTEXT_DATA =
        new ConcurrentHashMap<>();

    void add(final ServiceContext context) {
        final Bundle owner = context.owner();

        CONTEXT_DATA.put(owner.getBundleId(), context);
    }

    boolean has(final ServiceContext context) {
        final Bundle owner = context.owner();

        return CONTEXT_DATA.containsKey(owner.getBundleId());
    }

    void remove(final Bundle providerOrConsumer) {
        CONTEXT_DATA.remove(providerOrConsumer.getBundleId());
    }

    ServiceContext get(final Bundle owner) {
        Objects.requireNonNull(owner);
        return CONTEXT_DATA.getOrDefault(owner.getBundleId(), null);
    }
}
