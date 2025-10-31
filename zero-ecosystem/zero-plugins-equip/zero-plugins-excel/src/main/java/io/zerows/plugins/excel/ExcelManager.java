package io.zerows.plugins.excel;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-31
 */
class ExcelManager extends AddOnManager<ExcelClient> {
    private static final Cc<String, ExcelClient> CC_STORED = Cc.open();

    private static final ExcelManager INSTANCE = new ExcelManager();

    private ExcelManager() {
    }

    static ExcelManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, ExcelClient> stored() {
        return CC_STORED;
    }
}
