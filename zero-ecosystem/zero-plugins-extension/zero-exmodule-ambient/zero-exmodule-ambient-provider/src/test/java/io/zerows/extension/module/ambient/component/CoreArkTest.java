package io.zerows.extension.module.ambient.component;

import io.zerows.extension.module.ambient.domain.tables.pojos.XTenant;
import io.zerows.platform.apps.KApp;
import io.zerows.platform.apps.KArk;
import io.zerows.specification.app.HArk;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CoreArkTest {

    @Test
    void shouldSkipTenantMergeWhenAppTenantIsNull() {
        final HArk ark = KArk.of(new KApp("ambient-app").id("ambient-app"));
        final Set<HArk> arkSet = new LinkedHashSet<>();
        arkSet.add(ark);

        final ConcurrentMap<String, XTenant> tenantMap = new ConcurrentHashMap<>();
        tenantMap.put("tenant-1", new XTenant().setId("tenant-1"));

        final Set<HArk> result = CoreArk.build(arkSet, tenantMap);

        Assertions.assertSame(arkSet, result);
        Assertions.assertEquals("DEFAULT", ark.owner().owner());
    }
}
