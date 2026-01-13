package io.zerows.plugins.monitor.underway;

import io.zerows.plugins.monitor.client.QuotaValue;

/**
 * @author lang : 2025-12-30
 */
interface MOM {
    String NAME_ENV = "env";

    String DATABASE_ID = "env.DATABASE";
    String DATABASE = QuotaValue.QUOTA_NS_PREFIX + DATABASE_ID;
    String CLUSTER_ID = "env.CLUSTER";
    String CLUSTER = QuotaValue.QUOTA_NS_PREFIX + CLUSTER_ID;

    String NAME_CACHE = "cache";
    String CC_ID = "cache.CC";
    String CC = QuotaValue.QUOTA_NS_PREFIX + CC_ID;

    String NAME_TASK = "task";
    String TASK_ID = "task.TASK";
    String TASK = QuotaValue.QUOTA_NS_PREFIX + TASK_ID;
}
