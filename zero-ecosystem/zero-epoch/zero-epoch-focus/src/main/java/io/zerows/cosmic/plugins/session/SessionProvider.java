package io.zerows.cosmic.plugins.session;

import io.zerows.sdk.plugins.AddOn;
import jakarta.inject.Provider;

import java.util.Objects;

/**
 * 这种依赖注入的模式只能走 Default 的 Vertx 来实现
 *
 * @author lang : 2025-10-14
 */
public class SessionProvider implements Provider<SessionClient> {
    private final AddOn<SessionClient> addOn;

    public SessionProvider(final AddOn<SessionClient> addOn) {
        this.addOn = addOn;
    }

    @Override
    public SessionClient get() {
        return Objects.requireNonNull(this.addOn).createInstance();
    }
}
