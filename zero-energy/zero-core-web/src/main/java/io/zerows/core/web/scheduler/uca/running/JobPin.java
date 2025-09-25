package io.zerows.core.web.scheduler.uca.running;

import io.zerows.core.constant.configure.YmlCore;
import io.zerows.core.util.Ut;
import io.zerows.core.web.scheduler.eon.MessageOfJob;
import io.zerows.module.metadata.store.OZeroStore;
import io.zerows.module.metadata.uca.logging.OLog;

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
            LOGGER.info(MessageOfJob.PIN.PIN_CONFIG, CONFIG);
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
                STORE = new UnityStore();
            }
            return STORE;
        }
    }
}
