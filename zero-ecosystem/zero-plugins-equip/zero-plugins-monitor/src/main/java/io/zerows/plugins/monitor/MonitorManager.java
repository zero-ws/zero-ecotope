package io.zerows.plugins.monitor;

import io.vertx.core.Vertx;
import io.zerows.epoch.annotations.Monitor;
import io.zerows.epoch.management.OCacheClass;
import io.zerows.plugins.monitor.client.QuotaMetric;
import io.zerows.plugins.monitor.metadata.MonitorConstant;
import io.zerows.plugins.monitor.metadata.YmMonitor;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-12-29
 */
@Slf4j
class MonitorManager {
    private static final ConcurrentMap<Integer, YmMonitor> STORED = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Class<?>> SCANNED = new ConcurrentHashMap<>();

    private static MonitorManager INSTANCE;

    private MonitorManager() {
    }

    static MonitorManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MonitorManager();
        }
        return INSTANCE;
    }

    void configYaml(final Vertx vertx, final YmMonitor monitor) {
        if (monitor != null) {
            STORED.put(vertx.hashCode(), monitor);
        }
    }

    YmMonitor configYaml(final Vertx vertx) {
        return STORED.get(vertx.hashCode());
    }

    ConcurrentMap<String, Class<?>> classOf() {
        if (!SCANNED.isEmpty()) {
            return SCANNED;
        }
        final Set<Class<?>> clazzSet = this.classOfMonitor();
        log.info("{} 监控类 Quota 数量：{}", MonitorConstant.K_PREFIX_MOC, clazzSet.size());
        for (final Class<?> clazz : clazzSet) {
            final Monitor monitor = clazz.getDeclaredAnnotation(Monitor.class);
            Objects.requireNonNull(monitor);
            SCANNED.put(monitor.value(), clazz);
        }
        return SCANNED;
    }

    private Set<Class<?>> classOfMonitor() {
        final Set<Class<?>> clazzSet = OCacheClass.entireValue();
        return clazzSet.stream()
            .filter(each -> Ut.isImplement(each, QuotaMetric.class))
            .filter(each -> each.isAnnotationPresent(Monitor.class))
            .collect(Collectors.toSet());
    }
}
