package io.zerows.plugins.monitor.client;

import io.zerows.plugins.monitor.metadata.YmMonitor;

import java.util.Set;

/**
 * @author lang : 2025-12-29
 */
public interface QuotaValue {

    String QUOTA_DATA_NS = "io.zerows.monitor.";

    Set<YmMonitor.Role> ofRole();

    default Set<YmMonitor.Client> ofClient() {
        return Set.of();
    }
}
