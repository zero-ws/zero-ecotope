package io.zerows.osgi.assembly.service;

import io.zerows.component.log.OLog;
import io.zerows.support.Ut;
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
