package io.zerows.extension.module.integration.boot;

import io.zerows.extension.module.integration.common.IsConfig;
import io.zerows.extension.skeleton.metadata.MDModuleManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-23
 */
@Slf4j
public class MDIntegrationManager extends MDModuleManager<Boolean, IsConfig> {
    private static MDIntegrationManager INSTANCE;

    private MDIntegrationManager() {
        super(MID.BUNDLE_SYMBOLIC_NAME);
    }

    public static MDIntegrationManager of() {
        if (INSTANCE == null) {
            INSTANCE = new MDIntegrationManager();
        }
        return INSTANCE;
    }
}
