package io.zerows.epoch.corpus.metadata.store.service;

import io.zerows.epoch.corpus.metadata.osgi.service.EnergyService;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceInvocation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-07-01
 */
public class EnergyServiceManager implements EnergyService {
    private final ServiceForContext context = new ServiceForContext();
    private final ServiceForProvider provider = new ServiceForProvider();
    private final ServiceForConsumer consumer = new ServiceForConsumer();

    @Override
    public Set<ServiceInvocation> serviceSet() {
        return this.provider.get();
    }

    @Override
    public synchronized void addContext(final ServiceContext context) {
        final Bundle owner = context.owner();
        Objects.requireNonNull(owner);
        if (this.context.has(context)) {
            return;
        }
        this.logger().info("ServiceContext has been added, owner = {}, {}",
            owner.getSymbolicName(), owner.getBundleId());
        this.context.add(context);
    }

    @Override
    public synchronized ServiceContext getContext(final Bundle owner) {
        return this.context.get(owner);
    }

    @Override
    public synchronized void removeContext(final ServiceContext context) {
        final Bundle owner = context.owner();
        this.context.remove(owner);

        this.provider.remove(owner);

        this.consumer.remove(owner);
    }

    @Override
    public synchronized void addProviderService(final ServiceInvocation service) {
        if (this.provider.has(service)) {
            return;
        }
        this.logger().info("  -->  Service: \"{}\" has been added into manager, provider = {} {}",
            service.id(), service.provider().getSymbolicName(), service.provider().getBundleId());
        this.provider.add(service);
    }

    @Override
    public synchronized void removeProviderService(final ServiceInvocation service) {
        this.provider.remove(service);
    }

    @Override
    public synchronized void removeProvider(final Bundle provider) {
        this.provider.remove(provider);

        this.consumer.remove(provider);

        this.context.remove(provider);
    }

    @Override
    public synchronized Set<ServiceInvocation> getProviderService(final Bundle provider) {
        return this.provider.get(provider);
    }

    @Override
    public synchronized void addConsumerService(final Bundle consumer, final String serviceId) {
        if (this.consumer.has(consumer, serviceId)) {
            return;
        }
        this.logger().info("  <--  Service: \"{}\" consumer has been added, consumer = {} {}",
            serviceId, consumer.getSymbolicName(), consumer.getBundleId());
        this.consumer.add(consumer, serviceId);
    }

    @Override
    public synchronized void removeConsumerService(final Bundle consumer, final String serviceId) {
        this.consumer.remove(consumer, serviceId);
    }

    @Override
    public synchronized void removeConsumer(final Bundle consumer) {
        this.consumer.remove(consumer);

        this.provider.remove(consumer);

        this.context.remove(consumer);
    }

    @Override
    public synchronized Set<ServiceInvocation> getConsumerService(final Bundle consumer) {
        return this.consumer.get(consumer);
    }

    @Override
    public Set<Bundle> getConsumers(final String serviceId) {
        final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        return this.consumer.consumers(serviceId).stream()
            .map(context::getBundle)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
