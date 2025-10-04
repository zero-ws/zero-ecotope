package io.zerows.corpus.container;

import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-03
 */
class LinearCodexO implements StubLinear {

    private final HBundle bundle;

    LinearCodexO(final HBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void runUndeploy(final Class<?> clazz, final RunVertx runVertx) {
        if (Objects.isNull(this.bundle)) {
            return;
        }
        // TODO: OSGI 支持
    }

    @Override
    public void runDeploy(final Class<?> clazz, final RunVertx runVertx) {
        if (Objects.isNull(this.bundle)) {
            return;
        }
        // TODO: OSGI 支持
    }
}
