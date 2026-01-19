package io.zerows.plugins.weco;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

class WeComManager extends AddOnManager<WeComClient> {

    private static final Cc<String, WeComClient> CC_STORED = Cc.open();
    private static final WeComManager INSTANCE = new WeComManager();

    private WeComManager() {
    }

    static WeComManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, WeComClient> stored() {
        return CC_STORED;
    }
}
