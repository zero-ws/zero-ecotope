package io.zerows.extension.module.modulat.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;

/**
 * @author lang : 2025-12-23
 */
public class MDModulatManager extends MDModuleManager<Boolean, BkConfig> {
    private static MDModulatManager INSTANCE;

    private MDModulatManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDModulatManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDModulatManager();
        }
        return INSTANCE;
    }
}
