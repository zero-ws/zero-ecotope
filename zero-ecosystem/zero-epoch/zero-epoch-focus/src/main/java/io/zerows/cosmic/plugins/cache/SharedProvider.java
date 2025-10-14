package io.zerows.cosmic.plugins.cache;

import io.zerows.sdk.plugins.AddOn;
import jakarta.inject.Provider;

import java.util.Objects;

/**
 * 这种依赖注入的模式只能走 Default 的 Vertx 来实现
 *
 * @author lang : 2025-10-14
 */
@SuppressWarnings("all")
public class SharedProvider implements Provider<SharedClient> {
    private final AddOn<SharedClient> addOn;

    public SharedProvider(final AddOn<SharedClient> addOn) {
        this.addOn = addOn;
    }

    @Override
    public SharedClient get() {
        return Objects.requireNonNull(this.addOn).createInstance();
    }
}
