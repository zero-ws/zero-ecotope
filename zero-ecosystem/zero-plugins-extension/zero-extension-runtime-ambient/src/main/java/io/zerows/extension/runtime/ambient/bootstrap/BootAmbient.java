package io.zerows.extension.runtime.ambient.bootstrap;

import io.zerows.epoch.corpus.extension.AbstractBoot;
import io.zerows.extension.runtime.ambient.eon.AtConstant;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootAmbient extends AbstractBoot {
    public BootAmbient() {
        super(AtConstant.BUNDLE_SYMBOLIC_NAME);
    }
}
