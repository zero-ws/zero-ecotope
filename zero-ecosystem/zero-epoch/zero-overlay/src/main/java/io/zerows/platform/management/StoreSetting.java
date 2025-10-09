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
}
