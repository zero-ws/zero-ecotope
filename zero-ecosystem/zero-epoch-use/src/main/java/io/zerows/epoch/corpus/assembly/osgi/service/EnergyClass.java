package io.zerows.epoch.corpus.assembly.osgi.service;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;
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
