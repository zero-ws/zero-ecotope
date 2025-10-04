package io.zerows.sdk.osgi;

import io.r2mo.typed.cc.Cc;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 服务执行记录
 *
 * @author lang : 2024-07-01
 */
class ServiceExecuted {
    /**
     * 执行记录引用，键 key 值为 Bundle 的运行 id，此处执行引用只是为了引用执行记录中的全局执行记录辅助启动上线
     * <pre><code>
     *     Bundle 01 -> ServiceExecuted -> DATA 01                    DATA_GLOBAL
     *     Bundle 02 -> ServiceExecuted -> DATA 02                    DATA_GLOBAL
     *     Bundle 03 -> ServiceExecuted -> DATA 03                    DATA_GLOBAL
     * </code></pre>
     */
    private static final Cc<Long, ServiceExecuted> CC_EXECUTED = Cc.open();
    /**
     * 消费服务清单，用来判断当前 Bundle Id 中是否消费过某个服务，如果消费过某个服务，则直接不再调用服务或执行
     */
    private final ConcurrentMap<Long, Set<String>> executed = new ConcurrentHashMap<>();
    private final Bundle owner;

    private ServiceExecuted(final Bundle owner) {
        this.owner = owner;
    }

    static ServiceExecuted of(final Bundle owner) {
        return CC_EXECUTED.pick(() -> new ServiceExecuted(owner), owner.getBundleId());
    }

    Bundle owner() {
        return this.owner;
    }

    synchronized Set<String> getExecuted(final Long bundleId) {
        return this.executed.getOrDefault(bundleId, new HashSet<>());
    }

    synchronized void removeExecuted(final Long bundleId, final String serviceId) {
        this.executed.computeIfAbsent(bundleId, key -> new HashSet<>()).remove(serviceId);
        // 后执行不走，多一个
        if (this.executed.get(bundleId).isEmpty()) {
            this.executed.remove(bundleId);
        }
    }

    synchronized void addExecuted(final Long bundleId, final String serviceId) {
        this.executed.computeIfAbsent(bundleId, key -> new HashSet<>()).add(serviceId);
    }
}
