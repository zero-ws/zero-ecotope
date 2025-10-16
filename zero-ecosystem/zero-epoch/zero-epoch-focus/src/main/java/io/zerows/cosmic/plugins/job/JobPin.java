package io.zerows.cosmic.plugins.job;

public class JobPin {

    private static JobConfig CONFIG;
    private static JobStore STORE;

    public static JobConfig getConfig() {
        return CONFIG;
    }

    public static JobStore getStore() {
        /*
         * Singleton for UnityStore ( package scope )
         */
        synchronized (JobStore.class) {
            if (null == STORE) {
                STORE = new JobStoreUnity();
            }
            return STORE;
        }
    }
}
