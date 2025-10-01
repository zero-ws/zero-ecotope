package io.zerows.epoch.osgi.metadata.dependency;

import io.zerows.epoch.osgi.metadata.service.EnergyDeployment;
import io.zerows.epoch.sdk.metadata.dependency.OOnce;

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
