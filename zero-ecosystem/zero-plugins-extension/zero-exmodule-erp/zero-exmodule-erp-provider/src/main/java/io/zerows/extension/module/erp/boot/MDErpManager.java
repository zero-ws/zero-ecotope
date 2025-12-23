package io.zerows.extension.module.erp.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDErpManager extends MDModuleManager<Boolean, Boolean> {
    private static MDErpManager INSTANCE;

    private MDErpManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDErpManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDErpManager();
        }
        return INSTANCE;
    }
}
