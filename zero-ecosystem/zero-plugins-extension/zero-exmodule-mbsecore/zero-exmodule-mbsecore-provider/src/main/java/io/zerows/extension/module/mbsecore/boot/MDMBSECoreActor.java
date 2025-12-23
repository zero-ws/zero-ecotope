package io.zerows.extension.module.mbsecore.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.module.mbsecore.metadata.config.AoConfig;
import io.zerows.extension.skeleton.metadata.MDModuleActor;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "metamodel", sequence = 1017)
public class MDMBSECoreActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDMBSECoreManager manager() {
        return MDMBSECoreManager.of();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<AoConfig> typeOfConfiguration() {
        return AoConfig.class;
    }
}
