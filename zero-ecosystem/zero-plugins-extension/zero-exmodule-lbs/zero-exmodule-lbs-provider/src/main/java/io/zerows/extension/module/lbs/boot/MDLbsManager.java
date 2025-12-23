package io.zerows.extension.module.lbs.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDLbsManager extends MDModuleManager<Boolean, Boolean> {
    private static MDLbsManager INSTANCE;

    private MDLbsManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDLbsManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDLbsManager();
        }
        return INSTANCE;
    }
}
