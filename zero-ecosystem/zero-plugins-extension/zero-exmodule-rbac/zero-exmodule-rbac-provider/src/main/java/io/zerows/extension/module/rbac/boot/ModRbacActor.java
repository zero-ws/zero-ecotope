package io.zerows.extension.module.rbac.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.base.MDActorOfModule;

/**
 * @author lang : 2025-12-16
 */
@Actor(value = "extension", sequence = 211)
public class ModRbacActor extends MDActorOfModule {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }
}
