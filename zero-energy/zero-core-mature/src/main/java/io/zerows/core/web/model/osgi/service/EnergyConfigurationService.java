package io.zerows.core.web.model.osgi.service;

import io.zerows.core.web.model.store.module.OCacheConfiguration;
import io.zerows.module.metadata.atom.configuration.MDConfiguration;
import io.zerows.module.metadata.osgi.service.EnergyConfiguration;
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
