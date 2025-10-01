package io.zerows.epoch.corpus.container.uca.store;

import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.epoch.sdk.metadata.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-03
 */
class LinearCodex extends AbstractAmbiguity implements StubLinear {
    private final StubLinear linearApp;
    private final StubLinear linearOsgi;

    LinearCodex(final Bundle bundle) {
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
