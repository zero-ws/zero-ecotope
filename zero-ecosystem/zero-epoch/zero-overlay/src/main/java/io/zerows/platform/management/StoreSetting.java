package io.zerows.platform.management;

import io.r2mo.typed.cc.Cc;
import io.zerows.specification.configuration.HSetting;
import io.zerows.specification.development.compiled.HBundle;

/**
 * @author lang : 2025-10-09
 */
public interface StoreSetting extends OCache<HSetting> {
    Cc<String, StoreSetting> CC_SKELETON = Cc.openThread();

    static StoreSetting of(final HBundle bundle) {
        final String cacheKey = HBundle.id(bundle, StoreSettingAmbiguity.class);
        return CC_SKELETON.pick(() -> new StoreSettingAmbiguity(bundle), cacheKey);
    }

    static StoreSetting of() {
        return of(null);
    }

    @Override
    StoreSetting add(HSetting setting);

    StoreSetting bind(Class<?> bootCls, String id);

    /*
     * 针对 NodeNetwork 的存储，每个 Setting 中只能拥有一个 NodeNetwork 实例，此处只做管理，替换旧版的 NodeCache
     * 此处可针对 HSetting 进行统一的 Network 管理，每个 Network 中的基本结构和数量
     * - HttpServerOptions  x 1
     * - SockOptions        x 1  -> 插件模式
     * - JobOptions         x 1  -> 插件模式
     * - CorsOptions        x 1
     * - ClusterOptions     x 1
     */
    <T> T getNetwork(HSetting setting);

    <T> StoreSetting add(HSetting setting, T network);

    HSetting getBy(Class<?> bootCls);
}
