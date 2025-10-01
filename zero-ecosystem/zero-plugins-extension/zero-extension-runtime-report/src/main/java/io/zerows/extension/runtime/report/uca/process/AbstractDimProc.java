package io.zerows.extension.runtime.report.uca.process;

import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-10-29
 */
abstract class AbstractDimProc implements DimProc {

    private final Bundle owner;

    AbstractDimProc(final Bundle owner) {
        this.owner = owner;
    }

    static DimProc of(final Bundle owner, final Class<?> implClass) {
        final String keyCache = Ut.Bnd.keyCache(owner, implClass);
        return CC_SKELETON.pick(() -> Ut.instance(implClass, owner), keyCache);
    }

    protected Bundle owner() {
        return this.owner;
    }
}
