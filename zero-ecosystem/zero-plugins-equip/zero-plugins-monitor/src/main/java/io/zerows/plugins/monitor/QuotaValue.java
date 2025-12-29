package io.zerows.plugins.monitor;

import io.zerows.plugins.monitor.metadata.YmMonitor;

import java.util.Set;

/**
 * @author lang : 2025-12-29
 */
public interface QuotaValue {

    String QUOTA_DATA_NS = "io.zerows.monitor.";
    String QUOTA_DATA_CONFIG = QUOTA_DATA_NS + "config";

    Set<YmMonitor.Role> ofRoles();

    default Set<YmMonitor.Client> ofClients() {
        return Set.of();
    }
}
