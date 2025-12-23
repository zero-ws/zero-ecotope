package io.zerows.extension.module.ambient.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.base.MDActorOfModule;

/**
 * @author lang : 2025-12-15
 */
@Actor(value = "extension", sequence = 207)
public class ModAmbientActor extends MDActorOfModule {

    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }
}
