package io.zerows.plugins.email;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

/**
 * @author lang : 2025-10-14
 */
class EmailManager extends AddOnManager<EmailClient> {
    private static final Cc<String, EmailClient> CC_STORED = Cc.open();

    private static final EmailManager INSTANCE = new EmailManager();

    private EmailManager() {
    }

    static EmailManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, EmailClient> stored() {
        return CC_STORED;
    }
}
