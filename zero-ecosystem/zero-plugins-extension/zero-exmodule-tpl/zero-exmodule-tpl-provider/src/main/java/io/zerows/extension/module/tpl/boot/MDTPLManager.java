package io.zerows.extension.module.tpl.boot;

import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDTPLManager extends MDModuleManager<Boolean, Boolean> {
    private static MDTPLManager INSTANCE;

    private MDTPLManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDTPLManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDTPLManager();
        }
        return INSTANCE;
    }
}