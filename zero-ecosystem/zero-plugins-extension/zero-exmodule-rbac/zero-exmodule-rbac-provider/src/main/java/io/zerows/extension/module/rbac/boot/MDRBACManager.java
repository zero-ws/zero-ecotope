package io.zerows.extension.module.rbac.boot;

import io.zerows.extension.module.rbac.metadata.ScConfig;
import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDRBACManager extends MDModuleManager<Boolean, ScConfig> {
    private static MDRBACManager INSTANCE;

    private MDRBACManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDRBACManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDRBACManager();
        }
        return INSTANCE;
    }
}
