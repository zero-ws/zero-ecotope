package io.zerows.extension.module.ambient.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-22
 */
@Slf4j
public class MDAmbientManager extends MDModuleManager<Boolean, AtConfig> {
    private static MDAmbientManager INSTANCE;

    private MDAmbientManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDAmbientManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDAmbientManager();
        }
        return INSTANCE;
    }
}
