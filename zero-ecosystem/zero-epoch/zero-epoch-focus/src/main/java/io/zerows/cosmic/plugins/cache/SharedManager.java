package io.zerows.cosmic.plugins.cache;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-15
 */
class SharedManager extends AddOnManager<SharedClient> {
    private static final Cc<String, SharedClient> CC_STORED = Cc.open();

    private static final SharedManager INSTANCE = new SharedManager();

    private SharedManager() {
    }

    @Override
    protected Cc<String, SharedClient> stored() {
        return CC_STORED;
    }

    static SharedManager of() {
        return INSTANCE;
    }
}
