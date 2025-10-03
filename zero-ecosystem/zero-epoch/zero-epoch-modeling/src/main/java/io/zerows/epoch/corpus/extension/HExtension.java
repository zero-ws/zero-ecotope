package io.zerows.epoch.corpus.extension;


import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.component.normalize.EquipAt;
import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.configuration.module.MDConnect;
import io.zerows.platform.constant.VBoot;
import io.zerows.management.OZeroStore;
import io.zerows.epoch.program.Ut;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 扩展启动器，用于 Zero Extension 扩展模块专用，职责如下：
 * <pre><code>
 *     1. 负责扩展模块数据导入
 *     2. 负责扩展模块抓取 crud 部分配置
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HExtension {

    Cc<Class<?>, HExtension> CC_BOOT = Cc.open();

    static Set<HExtension> initialize() {
        /* Boot processing */
        final ConcurrentMap<Class<?>, HExtension> data = CC_BOOT.get();
        if (data.isEmpty()) {
            final HSetting setting = OZeroStore.setting();
            final JsonObject launcherJ = setting.launcher().options();
            final JsonArray boots = Ut.valueJArray(launcherJ, VBoot.EXTENSION);
            Ut.itJArray(boots).forEach(json -> {
                final Class<?> bootCls = Ut.clazz(json.getString(VBoot.extension.EXECUTOR), null);
                if (Objects.nonNull(bootCls)) {
                    CC_BOOT.pick(() -> Ut.instance(bootCls), bootCls);
                }
            });
        }
        return new HashSet<>(data.values());
    }

    static MDConfiguration getOrCreate(final String module) {
        return AbstractBoot.CONFIGURATION_MAP.computeIfAbsent(module, (moduleValue) -> {
            // 创建一个新的 MDConfiguration
            final MDConfiguration configuration = new MDConfiguration(moduleValue);
            // 对新的 MDConfiguration 执行初始化 -> 会写入到 MDConfiguration 缓存中
            final EquipAt component = EquipAt.of(configuration.id());
            component.initialize(configuration);
            // 初始化完成之后返回
            return configuration;
        });
    }

    static MDConfiguration getOrCreate(final Bundle owner) {
        return AbstractBoot.CONFIGURATION_MAP.computeIfAbsent(owner.getSymbolicName(), (moduleValue) -> {
            // 创建一个新的 MDConfiguration
            final MDConfiguration configuration = new MDConfiguration(owner);
            // 对新的 MDConfiguration 执行初始化 -> 会写入到 MDConfiguration 缓存中
            final EquipAt component = EquipAt.of(configuration.id());
            component.initialize(configuration);
            // 初始化完成之后返回
            return configuration;
        });
    }

    static Set<String> keySet() {
        return AbstractBoot.CONFIGURATION_MAP.keySet();
    }

    /**
     * Fast to extract {@link MDConnect} reference from stored.
     *
     * @param table table name of
     *
     * @return connect configuration reference
     */
    static MDConnect connect(final String table) {
        if (Ut.isNil(table)) {
            return null;
        }
        return CC_BOOT.get().values().stream()
            .flatMap(extension -> extension.connect().values().stream())
            .filter(connect -> table.equals(connect.getTable()))
            .findAny().orElse(null);
    }

    /*
     *  Following two methods are for data loading
     *  - First one is used by Excel Loader  ( zero-ifx-excel )
     *  - The second has been used by BtBoot ( zero-ke )
     */
    ConcurrentMap<String, MDConnect> connect();

    List<String> oob();

    List<String> oob(String prefix);

    /*
     * Following two methods are for Crud Default Value
     *  - First has been used by CRUD Extension ( zero-crud )
     *  - Second has been used by UI Extension  ( zero-ui )
     * They are not for data loading but for running usage
     */
    ConcurrentMap<String, JsonObject> module();

    ConcurrentMap<String, JsonArray> column();
}
