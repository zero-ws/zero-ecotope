package io.zerows.extension.module.report.plugins;

import io.zerows.extension.skeleton.spi.ExDefault;

import java.util.Set;

public class ExDefaultReport implements ExDefault {

    @Override
    public Set<String> ruleExclude() {
        return Set.of(
            "/api/report/single-of"
        );
    }
}
