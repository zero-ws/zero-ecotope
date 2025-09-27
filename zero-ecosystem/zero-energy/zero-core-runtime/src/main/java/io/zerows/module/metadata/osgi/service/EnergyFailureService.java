package io.zerows.module.metadata.osgi.service;

import io.vertx.core.json.JsonObject;
import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.spi.boot.HEquip;
import io.zerows.module.metadata.store.OCacheFailure;
import io.zerows.module.metadata.store.OZeroEquip;
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
