package io.zerows.extension.module.ui.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.base.MDActorOfModule;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 1017)
public class ModUiActor extends MDActorOfModule {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }
}
