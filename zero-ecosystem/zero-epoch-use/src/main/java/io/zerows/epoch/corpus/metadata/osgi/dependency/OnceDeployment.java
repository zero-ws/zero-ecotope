package io.zerows.epoch.corpus.metadata.osgi.dependency;

import io.zerows.epoch.corpus.metadata.osgi.service.EnergyDeployment;
import io.zerows.epoch.corpus.metadata.zdk.dependency.OOnce;

import java.util.Objects;

/**
 * @author lang : 2024-07-03
 */
public class OnceDeployment implements OOnce<EnergyDeployment> {
    // 等待服务
    private volatile EnergyDeployment cachedEnergyDeployment;

    @Override
    public void bind(final Object reference) {
        if (reference instanceof final EnergyDeployment energyDeployment) {
            this.cachedEnergyDeployment = energyDeployment;
        }
    }

    @Override
    public boolean isReady() {
        return Objects.nonNull(this.cachedEnergyDeployment);
    }

    @Override
    public EnergyDeployment reference() {
        return this.cachedEnergyDeployment;
    }
}
