package io.zerows.extension.mbse.modulat.bootstrap;

import io.zerows.core.web.model.extension.AbstractBoot;
import io.zerows.extension.mbse.modulat.eon.BkConstant;

import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootModulat extends AbstractBoot {
    public BootModulat() {
        super(BkConstant.BUNDLE_SYMBOLIC_NAME);
    }

    @Override
    protected Set<String> configureBuiltIn() {
        return BkPin.getBuiltIn();
    }
}
