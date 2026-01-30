package io.zerows.extension.module.ambient.component;

import io.r2mo.typed.domain.builder.BuilderOf;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.ambient.domain.tables.pojos.XApp;
import io.zerows.extension.module.ambient.domain.tables.pojos.XSource;
import io.zerows.extension.module.ambient.domain.tables.pojos.XTenant;
import io.zerows.platform.apps.KDS;
import io.zerows.platform.apps.KTenant;
import io.zerows.platform.management.StoreApp;
import io.zerows.platform.management.StoreArk;
import io.zerows.specification.app.HApp;
import io.zerows.specification.app.HArk;
import io.zerows.specification.app.HLot;
import io.zerows.support.Ut;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 底层对接
 * <pre>
 * - {@link StoreArk}
 * - {@link StoreApp}
 * </pre>
 */
public class CoreArk {

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

    /**
     * 合并函数
     * <pre>
     *     此处的租户必须是已存在的组户信息 X_TENANT 中有数据
     * </pre>
     *
     * @param arkSet    应用集合
     * @param tenantMap 组户表
     * @return 构造好的应用容器
     */
    public static Set<HArk> build(final Set<HArk> arkSet, final ConcurrentMap<String, XTenant> tenantMap) {
        if (tenantMap.isEmpty()) {
            return arkSet;
        }
        arkSet.forEach(ark -> {
            final HApp app = ark.app();
            final XTenant tenant = tenantMap.getOrDefault(app.tenant(), null);
            if (Objects.nonNull(tenant)) {
                // HLot 构造
                final JsonObject tenantJ = Ut.serializeJson(tenant);
                final HLot lot = KTenant.getOrCreate(tenant.getId());
                lot.data(tenantJ);
                ark.apply(lot);
            }
        });
        return arkSet;
    }

    /**
     * 合并函数
     * <pre>
     *     1. 查找已加载的 {@link HArk} 容器
     *        - 若不存在则创建新的
     *        - 存在则直接使用 XApp 中的数据更新
     *     2. 针对 {@link KDS} 数据源进行编排
     *        此处若存在动态数据源则需要执行动态数据源编排
     * </pre>
     *
     * @param app     应用结构
     * @param sources 数据源列表
     * @return 构造好的应用容器
     */
    public static HArk build(final XApp app, final List<XSource> sources) {
        // 构造新的 HArk
        final BuilderOf<HArk> builder = BuilderOf.of(BuilderOfHApp::new);
        final HArk ark = builder.create(app);
        // 更新 HArk 中的数据库信息
        builder.updateConditional(ark, sources);
        return ark;
    }
}
