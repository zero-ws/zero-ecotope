package io.zerows.epoch.corpus.metadata.osgi.dependency;

import io.zerows.epoch.corpus.metadata.osgi.service.EnergyService;
import io.zerows.epoch.corpus.metadata.zdk.dependency.OOnce;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;

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
