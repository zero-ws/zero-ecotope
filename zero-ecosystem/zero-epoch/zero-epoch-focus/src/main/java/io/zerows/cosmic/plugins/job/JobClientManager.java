package io.zerows.cosmic.plugins.job;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-17
 */
public class JobClientManager extends AddOnManager<JobClient> {
    private static final Cc<String, JobClient> CC_STORED = Cc.open();

    private static final JobClientManager INSTANCE = new JobClientManager();

    private JobClientManager() {
    }

    static JobClientManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, JobClient> stored() {
        return CC_STORED;
    }
}
