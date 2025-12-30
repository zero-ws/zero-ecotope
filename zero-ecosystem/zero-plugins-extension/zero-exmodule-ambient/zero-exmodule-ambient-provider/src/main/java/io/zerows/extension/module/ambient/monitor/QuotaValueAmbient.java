package io.zerows.extension.module.ambient.monitor;

import io.zerows.plugins.monitor.client.QuotaValueBase;

import java.util.Map;
import java.util.Set;

/**
 * @author lang : 2025-12-29
 */
public class QuotaValueAmbient extends QuotaValueBase {
    @Override
    protected Set<String> ofClientName() {
        return Set.of(
            MOM.APP
        );
    }

    @Override
    protected Map<String, String> ofRoleName() {
        return Map.of(
            MOM.APP_ID, MOM.APP
        );
    }
}