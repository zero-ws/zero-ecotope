package io.zerows.epoch.osgi.assembly.service;

import io.zerows.epoch.common.log.OLog;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-01
 */
public interface EnergyClass {

    void install(Bundle bundle);

    void uninstall(Bundle bundle);

    default OLog logger() {
        return Ut.Log.energy(this.getClass());
    }
}
