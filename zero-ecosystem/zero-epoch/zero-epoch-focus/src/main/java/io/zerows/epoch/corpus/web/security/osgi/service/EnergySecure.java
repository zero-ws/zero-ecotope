package io.zerows.epoch.corpus.web.security.osgi.service;

import io.zerows.component.log.OLog;
import io.zerows.epoch.program.Ut;
import org.osgi.framework.Bundle;

/**
 * @author lang : 2024-05-01
 */
public interface EnergySecure {

    void install(Bundle bundle);

    void uninstall(Bundle bundle);

    default OLog logger() {
        return Ut.Log.energy(this.getClass());
    }
}
