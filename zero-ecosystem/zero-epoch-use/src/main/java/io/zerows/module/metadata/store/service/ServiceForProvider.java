package io.zerows.module.metadata.store.service;

import io.zerows.module.metadata.zdk.service.ServiceInvocation;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-07-01
 */
class ServiceForProvider {
    // 提供者清单
    private static final ConcurrentMap<Long, Set<String>> PROVIDER_DATA = new ConcurrentHashMap<>();

    private final ServiceRegistry registry = new ServiceRegistry();
    private final ServiceForConsumer consumer = new ServiceForConsumer();

    void add(final ServiceInvocation invocation) {
        // 服务添加
        final String serviceId = this.registry.add(invocation);


        // 提供者服务上线
        final Long providerId = invocation.provider().getBundleId();
        PROVIDER_DATA.computeIfAbsent(providerId, key -> ConcurrentHashMap.newKeySet()).add(serviceId);
    }

    void remove(final ServiceInvocation invocation) {
        // 服务提供者，移除服务定义
        final String serviceId = this.registry.remove(invocation);


        // 提供者服务下线
        final Long providerId = invocation.provider().getBundleId();
        PROVIDER_DATA.computeIfAbsent(providerId, key -> ConcurrentHashMap.newKeySet()).remove(serviceId);


        // 消费者服务下线
        this.consumer.remove(serviceId);
    }

    boolean has(final ServiceInvocation invocation) {
        return PROVIDER_DATA.values().stream()
            .anyMatch(serviceIds -> serviceIds.contains(invocation.id()));
    }

    void remove(final Bundle provider) {
        Set<String> serviceIds = PROVIDER_DATA.remove(provider.getBundleId());
        if (Objects.isNull(serviceIds)) {
            serviceIds = new HashSet<>();
        }
        // 更新总体服务消费
        serviceIds.forEach(this.registry::remove);


        // 提供者下线
        PROVIDER_DATA.remove(provider.getBundleId());


        // 消费者服务下线
        this.consumer.remove(serviceIds);
    }

    Set<ServiceInvocation> get(final Bundle provider) {
        final Set<String> serviceIds = PROVIDER_DATA.get(provider.getBundleId());
        return serviceIds.stream().map(this.registry::get).collect(Collectors.toSet());
    }

    Set<ServiceInvocation> get() {
        return this.registry.get();
    }
}
