package io.zerows.plugins.integration.sms;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-17
 */
class SmsManager extends AddOnManager<SmsClient> {

    private static final Cc<String, SmsClient> CC_STORED = Cc.open();

    private static final SmsManager INSTANCE = new SmsManager();

    private SmsManager() {
    }

    @Override
    protected Cc<String, SmsClient> stored() {
        return CC_STORED;
    }

    static SmsManager of() {
        return INSTANCE;
    }
}