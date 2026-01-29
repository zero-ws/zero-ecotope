package io.zerows.extension.module.ambient.component;

import io.vertx.core.Future;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.module.ambient.domain.tables.pojos.XTenant;
import io.zerows.specification.app.HArk;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class CombineArk {

    public static Future<Set<HArk>> buildAsync(final Set<HArk> arkSet, final ConcurrentMap<String, XTenant> tenantMap) {
        return Future.succeededFuture(build(arkSet, tenantMap));
    }

    public static Set<HArk> build(final ConcurrentMap<String, XApp> apps, final ConcurrentMap<String, List<XSource>> children) {
        final Set<HArk> arkSet = new LinkedHashSet<>();
        apps.keySet().forEach(appId -> {
            final XApp app = apps.get(appId);
            final List<XSource> child = children.get(appId);
            arkSet.add(build(app, child));
        });
        return arkSet;
    }

    public static Future<Set<HArk>> buildAsync(final ConcurrentMap<String, XApp> apps, final ConcurrentMap<String, List<XSource>> children) {
        return Future.succeededFuture(build(apps, children));
    }

    public static Set<HArk> build(final Set<HArk> arkSet, final ConcurrentMap<String, XTenant> tenantMap) {

        return arkSet;
    }

    // 最小粒度
    public static HArk build(final XApp app, final List<XSource> sources) {
        return null;
    }
}
