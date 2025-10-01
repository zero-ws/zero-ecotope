package io.zerows.epoch.corpus.metadata.store.service;

import io.zerows.epoch.corpus.metadata.zdk.service.ServiceInvocation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务注册表
 *
 * @author lang : 2024-07-01
 */
class ServiceRegistry {

    private static final ConcurrentMap<String, ServiceInvocation> SERVICE_MAP =
        new ConcurrentHashMap<>();

    String add(final ServiceInvocation invocation) {
        Objects.requireNonNull(invocation);
        final String serviceId = invocation.id();
        Objects.requireNonNull(serviceId);
        // 更新服务清单
        SERVICE_MAP.put(serviceId, invocation);
        return serviceId;
    }

    String remove(final ServiceInvocation invocation) {
        Objects.requireNonNull(invocation);
        final String serviceId = invocation.id();
        return this.remove(serviceId);
    }

    String remove(final String serviceId) {
        Objects.requireNonNull(serviceId);
        // 更新服务清单
        SERVICE_MAP.remove(serviceId);
        return serviceId;
    }

    ServiceInvocation get(final String serviceId) {
        return SERVICE_MAP.getOrDefault(serviceId, null);
    }

    Set<ServiceInvocation> get() {
        return new HashSet<>(SERVICE_MAP.values());
    }
}
