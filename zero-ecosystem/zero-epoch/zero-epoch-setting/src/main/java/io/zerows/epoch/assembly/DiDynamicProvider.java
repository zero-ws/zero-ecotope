package io.zerows.epoch.assembly;

import com.google.inject.Key;
import com.google.inject.ProvisionException;
import jakarta.inject.Inject;
import jakarta.inject.Provider;

import java.util.Objects;

/**
 * @author lang : 2025-10-14
 */
public class DiDynamicProvider<T> implements Provider<T> {
    private final Key<T> key;

    @Inject
    DiDynamicProvider(final Key<T> key) {
        this.key = key;
    }

    @Override
    public T get() {
        final Provider<T> provider = DiRegistry.of().get(this.key);
        if (Objects.isNull(provider)) {
            throw new ProvisionException("[ ZERO ] 无法找到 Provider, key = " + this.key);
        }
        return provider.get();
    }
}
