package io.zerows.extension.crud.plugins;

import io.zerows.extension.skeleton.spi.ExDefault;

import java.util.Set;

public class ExDefaultCRUD implements ExDefault {
    @Override
    public Set<String> ruleExclude() {
        return Set.of(
            "/api/:actor/search",
            "/api/:actor/missing",
            "/api/:actor/existing",
            "/api/:actor/export",
            "/api/:actor/import"
        );
    }
}
