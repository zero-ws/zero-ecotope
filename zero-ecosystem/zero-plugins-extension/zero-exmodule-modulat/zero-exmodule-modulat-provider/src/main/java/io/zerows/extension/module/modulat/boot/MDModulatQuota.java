package io.zerows.extension.module.modulat.boot;

import io.zerows.plugins.monitor.QuotaValue;
import io.zerows.plugins.monitor.metadata.YmMonitor;

import java.util.Set;

/**
 * @author lang : 2025-12-29
 */
public class MDModulatQuota implements QuotaValue {
    @Override
    public Set<YmMonitor.Role> ofRoles() {
        return Set.of();
    }
}
