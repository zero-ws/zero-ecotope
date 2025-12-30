package io.zerows.extension.module.modulat.monitor;

import io.zerows.plugins.monitor.client.QuotaValueBase;

import java.util.Map;
import java.util.Set;

/**
 * @author lang : 2025-12-29
 */
public class QuotaValueModulat extends QuotaValueBase {
    @Override
    protected Set<String> ofClientName() {
        return Set.of(
            MOM.BAG_ADMIN
        );
    }

    @Override
    protected Map<String, String> ofRoleName() {
        return Map.of(
            "modulat.BAG-ADMIN", MOM.BAG_ADMIN
        );
    }
}
