package io.zerows.osgi.metadata.service;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.configure.YmlCore;
import io.zerows.management.OCacheFailure;
import io.zerows.management.OZeroEquip;
import io.zerows.spi.HEquip;
import io.zerows.specification.configuration.HConfig;
import io.zerows.specification.configuration.HSetting;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-04-17
 */
public class EnergyFailureService implements EnergyFailure {

    private static EnergyFailure HOST;

    static EnergyFailure singleton() {
        if (Objects.isNull(HOST)) {
            HOST = new EnergyFailureService();
        }
        return HOST;
    }

    @Override
    public void install(final Bundle bundle) {
        final OCacheFailure cache = OCacheFailure.of(bundle);
        cache.add(this.ofError(bundle));
    }

    @Override
    public void uninstall(final Bundle bundle) {
        final OCacheFailure cache = OCacheFailure.of(bundle);
        cache.remove(this.ofError(bundle));
    }

    private JsonObject ofError(final Bundle bundle) {
        final HEquip equip = OZeroEquip.of(bundle);
        final HSetting setting = equip.initialize();
        final HConfig error = setting.infix(YmlCore.error.__KEY);
        return error.options();
    }
}
