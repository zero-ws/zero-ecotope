package io.zerows.extension.module.finance.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDFinanceManager extends MDModuleManager<Boolean, Boolean> {
    private static MDFinanceManager INSTANCE;

    private MDFinanceManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDFinanceManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDFinanceManager();
        }
        return INSTANCE;
    }
}
