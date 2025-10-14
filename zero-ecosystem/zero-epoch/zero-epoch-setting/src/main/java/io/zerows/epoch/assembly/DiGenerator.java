package io.zerows.epoch.assembly;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOn;
import jakarta.inject.Provider;

import java.util.Objects;
import java.util.Set;

public final class DiGenerator {

    private static final Cc<Class<?>, Provider<?>> CC_PROVIDER = Cc.open();
    private static final Cc<Class<?>, AddOn<?>> CC_ADDON = Cc.open();
    private static final Cc<String, DiGenerator> CC_SKELETON = Cc.openThread();

    private DiGenerator() {
    }

    public static DiGenerator of() {
        return CC_SKELETON.pick(DiGenerator::new);
    }

    public Set<Class<?>> keySet() {
        return CC_PROVIDER.keySet();
    }

    /** 缓存一个 Provider */
    public <T> void put(final Class<T> type, final Provider<? extends T> provider) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(provider, "provider");
        // 泛型擦除下的安全转型
        @SuppressWarnings("unchecked") final Provider<T> p = (Provider<T>) provider;
        CC_PROVIDER.put(type, p);
    }

    public <T> void put(final Class<T> type, final AddOn<T> addon) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(addon, "addon");
        // 泛型擦除下的安全转型
        CC_ADDON.put(type, addon);
    }

    public <T> AddOn<T> getAddOn(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        @SuppressWarnings("unchecked") final AddOn<T> addon = (AddOn<T>) CC_ADDON.get(type);
        return addon;
    }

    /** 取出缓存的 Provider（可能为 null） */
    public <T> Provider<T> getProvider(final Class<T> type) {
        Objects.requireNonNull(type, "type");
        @SuppressWarnings("unchecked") final Provider<T> p = (Provider<T>) CC_PROVIDER.get(type);
        return p;
    }
}

