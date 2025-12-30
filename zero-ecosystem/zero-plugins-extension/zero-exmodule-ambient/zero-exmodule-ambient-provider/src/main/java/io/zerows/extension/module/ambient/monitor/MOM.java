package io.zerows.extension.module.ambient.monitor;

import io.zerows.plugins.monitor.client.QuotaValue;

/**
 * @author lang : 2025-12-29
 */
interface MOM {
    String NAME_SCOPED = "scoped";

    String APP_ID = "scoped.APP";
    String APP = QuotaValue.QUOTA_NS_PREFIX + APP_ID;
}
