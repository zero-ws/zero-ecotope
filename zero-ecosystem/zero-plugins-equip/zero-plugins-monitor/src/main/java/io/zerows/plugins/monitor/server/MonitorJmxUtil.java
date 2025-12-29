package io.zerows.plugins.monitor.server;

import io.zerows.plugins.monitor.metadata.MonitorType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-12-29
 */
class MonitorJmxUtil {
    static ConcurrentMap<MonitorType, Supplier<MonitorJmxConnector>> SUPPLIER = new ConcurrentHashMap<>() {
        {
            this.put(MonitorType.HAWTIO, MonitorJmxHawtIo::new);
        }
    };
}
