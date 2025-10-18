package io.zerows.epoch.store;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-18
 */
class DBSManager extends AddOnManager<DBClient> {
    private static final Cc<String, DBClient> CC_STORED = Cc.open();
    private static final DBSManager INSTANCE = new DBSManager();

    private DBSManager() {
    }

    static DBSManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, DBClient> stored() {
        return CC_STORED;
    }
}
