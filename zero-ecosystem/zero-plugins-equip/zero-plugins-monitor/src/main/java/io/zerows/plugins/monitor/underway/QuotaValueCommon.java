package io.zerows.plugins.monitor.underway;

import io.zerows.plugins.monitor.client.QuotaValueBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lang : 2025-12-30
 */
public class QuotaValueCommon extends QuotaValueBase {
    @Override
    protected Set<String> ofClientName() {
        return Set.of(
            MOM.DATABASE,
            MOM.CLUSTER,
            MOM.CC
        );
    }

    @Override
    protected Map<String, String> ofRoleName() {
        final Map<String, String> roleMap = new HashMap<>();
        roleMap.put(MOM.DATABASE_ID, MOM.DATABASE);
        roleMap.put(MOM.CLUSTER_ID, MOM.CLUSTER);
        roleMap.put(MOM.CC_ID, MOM.CC);
        return roleMap;
    }

    @Override
    protected Map<String, Integer> ofRoleAt() {
        final Map<String, Integer> roleAtMap = new HashMap<>();
        roleAtMap.put(MOM.CC_ID, 300);      // 300 秒
        return roleAtMap;
    }
}
