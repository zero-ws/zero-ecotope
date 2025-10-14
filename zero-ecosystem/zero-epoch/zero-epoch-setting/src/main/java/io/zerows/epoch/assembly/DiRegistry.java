package io.zerows.epoch.assembly;

import com.google.inject.Key;
import io.r2mo.typed.cc.Cc;
import jakarta.inject.Provider;

/**
 * @author lang : 2025-10-14
 */
public final class DiRegistry {
    private static final Cc<Key<?>, Provider<?>> CC_PROVIDER = Cc.open();
    private static final Cc<String, DiRegistry> CC_SKELETON = Cc.openThread();

    private DiRegistry() {

    }

    public static DiRegistry of() {
        return CC_SKELETON.pick(DiRegistry::new);
    }

    public <T> void put(final Key<T> key, final Provider<? extends T> p) {
        CC_PROVIDER.put(key, p);
    }

    @SuppressWarnings("unchecked")
    public <T> Provider<T> get(final Key<T> key) {
        return (Provider<T>) CC_PROVIDER.get(key);
    }
}
