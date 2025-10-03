package io.zerows.osgi.metadata.dependency;

import io.zerows.osgi.metadata.service.EnergyService;
import io.zerows.sdk.osgi.OOnce;
import io.zerows.sdk.osgi.ServiceContext;

import java.util.Objects;

/**
 * @author lang : 2024-07-02
 */
public class OnceService implements OOnce<EnergyService> {
    private volatile EnergyService cachedService;
    private volatile ServiceContext.OK cachedSignal;

    @Override
    public void bind(final Object reference) {
        if (reference instanceof final EnergyService energyService) {
            this.cachedService = energyService;
        } else if (reference instanceof final ServiceContext.OK signal) {
            this.cachedSignal = signal;
        }
    }

    @Override
    public boolean isReady() {
        return Objects.nonNull(this.cachedService)
            && Objects.nonNull(this.cachedSignal);
    }

    @Override
    public EnergyService reference() {
        return this.cachedService;
    }
}
