package io.zerows.corpus.plugins.job;

import io.zerows.component.log.OLog;
import io.zerows.epoch.application.YmlCore;
import io.zerows.management.OZeroStore;
import io.zerows.support.Ut;

public class JobPin {

    private static final OLog LOGGER = Ut.Log.configure(JobPin.class);
    private static JobConfig CONFIG;
    private static JobStore STORE;

    static {
        if (OZeroStore.is(YmlCore.job.__KEY)) {
            CONFIG = OZeroStore.option(YmlCore.job.__KEY, JobConfig.class, JobConfig::new);
            //            final JsonObject job = config.getJsonObject(YmlCore.job.__KEY);
            //            if (!Ut.isNil(job)) {
            //                /* Extension job-get */
            //                CONFIG = Ut.deserialize(job, JobConfig.class);
            //            } else {
            //                CONFIG = new JobConfig();
            //            }
            LOGGER.info(JobMessage.PIN.PIN_CONFIG, CONFIG);
        }
    }

    public static JobConfig getConfig() {
        return CONFIG;
    }

    public static JobStore getStore() {
        /*
         * Singleton for UnityStore ( package scope )
         */
        synchronized (JobStore.class) {
            if (null == STORE) {
                STORE = new StoreUnity();
            }
            return STORE;
        }
    }
}
