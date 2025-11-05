package io.zerows.extension.module.integration.boot;

import io.zerows.cortex.extension.AbstractBoot;
import io.zerows.extension.module.integration.common.IsConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootIntegration extends AbstractBoot {
    public BootIntegration() {
        super(IsConstant.BUNDLE_SYMBOLIC_NAME);
    }
}
