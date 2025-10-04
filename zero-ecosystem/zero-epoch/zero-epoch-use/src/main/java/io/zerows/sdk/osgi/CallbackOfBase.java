package io.zerows.sdk.osgi;

import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-04-17
 */
public class CallbackOfBase implements OCallback.Standard {

    private final Bundle bundle;

    public CallbackOfBase(final Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public void start(final Object registry) {
        // 注册服务
        if (registry instanceof final EnergyFailure desk) {
            desk.install(this.bundle);
        }
    }

    @Override
    public void stop(final Object registry) {
        // 注销服务
        if (registry instanceof final EnergyFailure desk) {
            desk.uninstall(this.bundle);
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }
}
