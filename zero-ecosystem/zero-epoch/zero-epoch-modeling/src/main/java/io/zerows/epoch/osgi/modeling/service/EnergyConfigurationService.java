package io.zerows.epoch.osgi.modeling.service;

import io.zerows.epoch.configuration.module.MDConfiguration;
import io.zerows.epoch.mem.module.OCacheConfiguration;
import io.zerows.epoch.osgi.metadata.service.EnergyConfiguration;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-07-01
 */
public class EnergyConfigurationService implements EnergyConfiguration {

    @Override
    public EnergyConfiguration addConfiguration(final MDConfiguration configuration) {
        final OCacheConfiguration configurer = OCacheConfiguration.of(configuration.id().owner());
        configurer.add(configuration);
        return this;
    }

    @Override
    public EnergyConfiguration addSetting(final Bundle owner, final HSetting setting) {
        EnergyConfiguration.DATA_SETTING.put(owner.getBundleId(), setting);
        return this;
    }

    @Override
    public EnergyConfiguration removeConfiguration(final MDConfiguration configuration) {
        final OCacheConfiguration configurer = OCacheConfiguration.of(configuration.id().owner());
        configurer.remove(configuration);
        return this;
    }

    @Override
    public EnergyConfiguration removeSetting(final Bundle owner) {
        EnergyConfiguration.DATA_SETTING.remove(owner.getBundleId());
        return this;
    }

    @Override
    public MDConfiguration getConfiguration(final Bundle owner) {
        final OCacheConfiguration configurer = OCacheConfiguration.of(owner);
        return configurer.valueGet(owner.getSymbolicName());
    }

    @Override
    public HSetting getSetting(final Bundle owner) {
        return EnergyConfiguration.DATA_SETTING.getOrDefault(owner.getBundleId(), null);
    }
}
