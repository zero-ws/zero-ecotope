package io.zerows.plugins.store.liquibase;

import io.zerows.epoch.based.constant.KName;
import io.zerows.component.log.OLog;
import io.zerows.epoch.program.Ut;
import io.zerows.runtime.HMacrocosm;

import java.util.Properties;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class LiquibaseEncryption extends Properties {
    private static final OLog LOGGER = Ut.Log.plugin(LiquibaseEncryption.class);

    public LiquibaseEncryption() {
        super.defaults = new Properties();
    }

    @Override
    public synchronized Object put(final Object paramK, final Object paramV) {
        final Boolean enabled = Ut.envWith(HMacrocosm.HED_ENABLED, false, Boolean.class);
        LOGGER.info("[ HED ] Encrypt of HED enabled: {0}", enabled);
        if (KName.PASSWORD.equals(paramK) && enabled) {
            // HED_ENABLED=true
            final String decryptPassword = Ut.decryptRSAV(paramV.toString());
            return super.defaults.put(paramK, decryptPassword);
        } else {
            return super.defaults.put(paramK, paramV);
        }
    }
}
