package io.zerows.extension.runtime.crud.bootstrap;

import io.zerows.epoch.corpus.extension.AbstractBoot;
import io.zerows.extension.runtime.crud.eon.IxConstant;

/**
 * @author lang : 2024-06-17
 */
public class BootLauncher extends AbstractBoot {

    public BootLauncher() {
        super(IxConstant.ENTRY_CONFIGURATION);
    }
}
