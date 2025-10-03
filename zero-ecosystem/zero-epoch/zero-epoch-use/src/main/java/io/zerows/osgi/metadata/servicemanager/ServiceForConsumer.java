package io.zerows.osgi.metadata.servicemanager;

import io.zerows.epoch.sdk.osgi.ServiceInvocation;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2024-07-01
 */
class ServiceForConsumer {
    // 消费者清单
    private static final ConcurrentMap<Long, Set<String>> CONSUMER_DATA = new ConcurrentHashMap<>();

    private final ServiceRegistry registry = new ServiceRegistry();

    void add(final Bundle consumer, final String serviceId) {
        CONSUMER_DATA.computeIfAbsent(consumer.getBundleId(), key -> new HashSet<>()).add(serviceId);
    }

    boolean has(final Bundle consumer, final String serviceId) {
        return CONSUMER_DATA.computeIfAbsent(consumer.getBundleId(), key -> new HashSet<>()).contains(serviceId);
    }

    // 移除单个服务
    void remove(final String serviceId) {
        final Set<Long> consumers = CONSUMER_DATA.keySet();
        consumers.forEach(consumerId -> CONSUMER_DATA.computeIfAbsent(consumerId, key -> new HashSet<>()).remove(serviceId));
    }

    // 移除某个消费者的单个服务
    void remove(final Bundle consumer, final String serviceId) {
        CONSUMER_DATA.computeIfAbsent(consumer.getBundleId(), key -> new HashSet<>()).remove(serviceId);
    }

    // 移除多个服务
    void remove(final Set<String> serviceIds) {
        final Set<Long> consumers = CONSUMER_DATA.keySet();
        consumers.forEach(consumerId -> CONSUMER_DATA.computeIfAbsent(consumerId, key -> new HashSet<>()).removeAll(serviceIds));
    }

    // 移除单个消费者
    void remove(final Bundle consumer) {
        CONSUMER_DATA.remove(consumer.getBundleId());
    }

    Set<ServiceInvocation> get(final Bundle consumer) {
        final Set<String> serviceIds = CONSUMER_DATA.get(consumer.getBundleId());
        return serviceIds.stream().map(this.registry::get).collect(Collectors.toSet());
    }

    Set<Long> consumers(final String serviceId) {
        return CONSUMER_DATA.keySet().stream().filter(consumerId -> {
            final Set<String> services = CONSUMER_DATA.getOrDefault(consumerId, new HashSet<>());
            return services.contains(serviceId);
        }).collect(Collectors.toSet());
    }
}
