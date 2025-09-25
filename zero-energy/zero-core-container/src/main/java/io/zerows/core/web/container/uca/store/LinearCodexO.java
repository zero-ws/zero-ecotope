package io.zerows.core.web.container.uca.store;

import io.zerows.core.web.model.atom.running.RunVertx;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-03
 */
class LinearCodexO implements StubLinear {

    private final Bundle bundle;

    LinearCodexO(final Bundle bundle) {
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
