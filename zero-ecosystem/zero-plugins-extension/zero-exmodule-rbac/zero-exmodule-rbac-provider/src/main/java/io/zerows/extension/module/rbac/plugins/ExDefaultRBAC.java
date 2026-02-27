package io.zerows.extension.module.rbac.plugins;

import io.zerows.extension.skeleton.spi.ExDefault;

import java.util.Set;

public class ExDefaultRBAC implements ExDefault {
    @Override
    public Set<String> ruleExclude() {
        return Set.of(
            "/api/user/search/:identifier"
        );
    }
}
