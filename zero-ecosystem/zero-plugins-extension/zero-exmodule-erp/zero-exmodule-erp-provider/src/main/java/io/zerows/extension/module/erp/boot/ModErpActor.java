package io.zerows.extension.module.erp.boot;

import io.zerows.epoch.annotations.Actor;
import io.zerows.extension.skeleton.metadata.base.MDActorOfModule;

/**
 * @author lang : 2025-11-04
 */
@Actor(value = "extension", sequence = 1017)
public class ModErpActor extends MDActorOfModule {
    @Override
    protected String MID() {
        return MID.BUNDLE_SYMBOLIC_NAME;
    }
}
