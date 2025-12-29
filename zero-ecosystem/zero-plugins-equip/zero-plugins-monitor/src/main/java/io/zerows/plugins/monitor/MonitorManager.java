package io.zerows.plugins.monitor;

import io.vertx.core.Vertx;
import io.zerows.plugins.monitor.metadata.YmMonitor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-12-29
 */
class MonitorManager {
    private static final ConcurrentMap<Integer, YmMonitor> STORED = new ConcurrentHashMap<>();

    private static MonitorManager INSTANCE;

    private MonitorManager() {
    }

    static MonitorManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MonitorManager();
        }
        return INSTANCE;
    }

    void registry(final Vertx vertx, final YmMonitor monitor) {
        if (monitor != null) {
            STORED.put(vertx.hashCode(), monitor);
        }
    }

    YmMonitor get(final Vertx vertx) {
        return STORED.get(vertx.hashCode());
    }
}
