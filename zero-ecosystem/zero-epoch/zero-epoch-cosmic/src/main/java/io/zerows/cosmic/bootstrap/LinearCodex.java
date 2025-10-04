package io.zerows.cosmic.bootstrap;

import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-03
 */
class LinearCodex extends AbstractAmbiguity implements StubLinear {
    private final StubLinear linearApp;
    private final StubLinear linearOsgi;

    LinearCodex(final HBundle bundle) {
        super(bundle);
        this.linearApp = new LinearCodexA();
        this.linearOsgi = new LinearCodexO(bundle);
    }

    @Override
    public void initialize(final Set<Class<?>> classSet, final RunVertx runVertx) {
        this.runDeploy(null, runVertx);
    }

    @Override
    public void runUndeploy(final Class<?> clazz, final RunVertx runVertx) {
        if (Objects.isNull(this.caller())) {

            this.linearApp.runUndeploy(clazz, runVertx);
        } else {

            this.linearOsgi.runUndeploy(clazz, runVertx);
        }
    }

    @Override
    public void runDeploy(final Class<?> clazz, final RunVertx runVertx) {
        if (Objects.isNull(this.caller())) {

            this.linearApp.runDeploy(clazz, runVertx);
        } else {

            this.linearOsgi.runDeploy(clazz, runVertx);
        }
    }
}
