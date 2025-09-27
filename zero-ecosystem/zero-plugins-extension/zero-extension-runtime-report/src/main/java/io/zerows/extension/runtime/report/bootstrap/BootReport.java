package io.zerows.extension.runtime.report.bootstrap;

import io.zerows.core.web.model.extension.AbstractBoot;
import io.zerows.extension.runtime.report.eon.RpConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootReport extends AbstractBoot {
    public BootReport() {
        super(RpConstant.BUNDLE_SYMBOLIC_NAME);
    }
}
