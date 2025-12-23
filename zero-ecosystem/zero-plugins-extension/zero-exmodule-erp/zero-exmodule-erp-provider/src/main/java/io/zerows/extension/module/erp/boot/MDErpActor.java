package io.zerows.extension.module.erp.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.MDModuleActor;

/**
 * @author lang : 2025-11-04
 */
@Actor(value = "extension", sequence = 1017, configured = false)
public class MDErpActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDErpManager manager() {
        return MDErpManager.of();
    }
}
