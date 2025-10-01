package io.zerows.extension.runtime.workflow.bootstrap;

import io.zerows.epoch.corpus.extension.AbstractBoot;
import io.zerows.extension.runtime.workflow.eon.WfConstant;

import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class BootWorkflow extends AbstractBoot {
    public BootWorkflow() {
        super(WfConstant.BUNDLE_SYMBOLIC_NAME);
    }

    @Override
    protected Set<String> configureBuiltIn() {
        return WfPin.getBuiltIn();
    }
}
