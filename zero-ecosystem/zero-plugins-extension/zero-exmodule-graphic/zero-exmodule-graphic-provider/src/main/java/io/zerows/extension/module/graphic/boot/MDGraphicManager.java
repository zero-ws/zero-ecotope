package io.zerows.extension.module.graphic.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDGraphicManager extends MDModuleManager<Boolean, Boolean> {
    private static MDGraphicManager INSTANCE;

    private MDGraphicManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDGraphicManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDGraphicManager();
        }
        return INSTANCE;
    }
}
