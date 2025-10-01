package io.zerows.epoch.osgi.metadata.dependency;

import io.zerows.epoch.osgi.metadata.service.EnergyService;
import io.zerows.epoch.sdk.metadata.dependency.OOnce;

import java.util.Objects;

/**
 * @author lang : 2024-07-02
 */
public class OnceServiceContext implements OOnce<EnergyService> {
    private volatile EnergyService cachedService;

    @Override
    public void bind(final Object reference) {
        if (reference instanceof final EnergyService energyService) {
            this.cachedService = energyService;
        }
    }

    @Override
    public boolean isReady() {
        return Objects.nonNull(this.cachedService);
    }

    @Override
    public EnergyService reference() {
        return this.cachedService;
    }
}
