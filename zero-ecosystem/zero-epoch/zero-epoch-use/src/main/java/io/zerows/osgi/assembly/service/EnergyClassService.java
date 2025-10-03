package io.zerows.osgi.assembly.service;

import io.zerows.management.ORepositoryClass;
import io.zerows.epoch.sdk.management.ORepository;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-01
 */
public class EnergyClassService implements EnergyClass {
    @Override
    public void install(final Bundle bundle) {

        ORepository.ofOr(ORepositoryClass.class, bundle)
            .whenUpdate(null);

        this.logger().info("The configuration of class has been initialized!! Bundle = {}",
            bundle.getSymbolicName());
    }

    @Override
    public void uninstall(final Bundle bundle) {

        ORepository.ofOr(ORepositoryClass.class, bundle)
            .whenRemove();

        this.logger().info("Removed the configuration of class !! Bundle = {}",
            bundle.getSymbolicName());
    }
}
