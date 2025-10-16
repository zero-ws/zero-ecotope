package io.zerows.sdk.plugins;

import jakarta.inject.Provider;

import java.util.Objects;

/**
 * @author lang : 2025-10-16
 */
public abstract class AddOnProvider<DI> implements Provider<DI> {

    private final AddOn<DI> addOn;

    protected AddOnProvider(AddOn<DI> addOn) {
        this.addOn = Objects.requireNonNull(addOn);
    }

    @Override
    public DI get() {
        return this.addOn.createInstance();
    }
}
