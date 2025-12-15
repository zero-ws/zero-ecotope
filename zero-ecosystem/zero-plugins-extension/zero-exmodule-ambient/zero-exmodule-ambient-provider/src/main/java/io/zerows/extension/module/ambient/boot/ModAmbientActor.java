package io.zerows.extension.module.ambient.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.MDModuleActor;

/**
 * @author lang : 2025-12-15
 */
@Actor(value = "extension", sequence = 211)
public class ModAmbientActor extends MDModuleActor {
    
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }
}
