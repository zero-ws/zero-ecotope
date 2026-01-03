package io.zerows.plugins.cache;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2026-01-02
 */
class CachedManager extends AddOnManager<CachedClient> {
    private static final Cc<String, CachedClient> CC_STORED = Cc.open();

    private static final CachedManager INSTANCE = new CachedManager();

    private CachedManager() {
    }

    static CachedManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, CachedClient> stored() {
        return CC_STORED;
    }
}
