package io.zerows.extension.module.workflow.boot;

import io.zerows.cortex.extension.AbstractBoot;
import io.zerows.extension.module.workflow.common.WfConstant;

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
