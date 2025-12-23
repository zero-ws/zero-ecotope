package io.zerows.extension.module.mbsecore.boot;

import io.zerows.extension.module.mbsecore.metadata.config.AoConfig;
import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDMBSECoreManager extends MDModuleManager<Boolean, AoConfig> {

    public static MDMBSECoreManager INSTANCE;

    private MDMBSECoreManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDMBSECoreManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDMBSECoreManager();
        }
        return INSTANCE;
    }
}
