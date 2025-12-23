package io.zerows.extension.module.rbac.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.MDModuleActor;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 211)
public class ModRbacActor extends MDModuleActor {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }
}
