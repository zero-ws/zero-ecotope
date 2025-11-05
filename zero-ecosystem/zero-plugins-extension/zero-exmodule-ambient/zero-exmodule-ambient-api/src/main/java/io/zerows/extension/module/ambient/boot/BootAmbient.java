package io.zerows.extension.module.ambient.boot;

import io.zerows.cortex.extension.AbstractBoot;
import io.zerows.extension.module.ambient.common.AtConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootAmbient extends AbstractBoot {
    public BootAmbient() {
        super(AtConstant.BUNDLE_SYMBOLIC_NAME);
    }
}
