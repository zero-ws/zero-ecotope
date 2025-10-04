package io.zerows.extension.runtime.report.uca.process;

import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-10-29
 */
abstract class AbstractDimProc implements DimProc {

    private final HBundle owner;

    AbstractDimProc(final HBundle owner) {
        this.owner = owner;
    }

    static DimProc of(final HBundle owner, final Class<?> implClass) {
        final String keyCache = HBundle.id(owner, implClass);
        return CC_SKELETON.pick(() -> Ut.instance(implClass, owner), keyCache);
    }

    protected HBundle owner() {
        return this.owner;
    }
}
