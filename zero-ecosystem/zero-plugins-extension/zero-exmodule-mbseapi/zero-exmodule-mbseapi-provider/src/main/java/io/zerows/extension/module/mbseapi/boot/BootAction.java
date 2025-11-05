package io.zerows.extension.module.mbseapi.boot;

import io.zerows.cortex.extension.AbstractBoot;
import io.zerows.extension.module.mbseapi.metadata.JtConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootAction extends AbstractBoot {
    public BootAction() {
        super(JtConstant.BUNDLE_SYMBOLIC_NAME);
    }
}
