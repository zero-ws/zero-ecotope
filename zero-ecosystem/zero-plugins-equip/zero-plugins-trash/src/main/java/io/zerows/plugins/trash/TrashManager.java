package io.zerows.plugins.trash;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-17
 */
class TrashManager extends AddOnManager<TrashClient> {

    private static final Cc<String, TrashClient> CC_STORED = Cc.open();

    private static final TrashManager INSTANCE = new TrashManager();

    private TrashManager() {
    }

    @Override
    protected Cc<String, TrashClient> stored() {
        return CC_STORED;
    }

    static TrashManager of() {
        return INSTANCE;
    }
}
