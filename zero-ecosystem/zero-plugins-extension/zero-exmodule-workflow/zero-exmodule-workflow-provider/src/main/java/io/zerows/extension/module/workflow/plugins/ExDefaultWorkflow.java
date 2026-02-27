package io.zerows.extension.module.workflow.plugins;

import io.zerows.extension.skeleton.spi.ExDefault;

import java.util.Set;

public class ExDefaultWorkflow implements ExDefault {
    @Override
    public Set<String> ruleExclude() {
        return Set.of(
            "/api/up/flow-queue",
            "/api/up/flow-history"
        );
    }
}
