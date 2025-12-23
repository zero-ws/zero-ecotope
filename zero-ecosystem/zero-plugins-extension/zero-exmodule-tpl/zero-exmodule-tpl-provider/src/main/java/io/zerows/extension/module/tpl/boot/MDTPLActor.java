package io.zerows.extension.module.tpl.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.MDModuleActor;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 1017, configured = false)
public class MDTPLActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected MDTPLManager manager() {
        return MDTPLManager.of();
    }
}
