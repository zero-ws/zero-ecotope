package io.zerows.plugins.weco;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

class WeChatManager extends AddOnManager<WeChatClient> {

    private static final Cc<String, WeChatClient> CC_STORED = Cc.open();
    private static final WeChatManager INSTANCE = new WeChatManager();

    private WeChatManager() {
    }

    static WeChatManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, WeChatClient> stored() {
        return CC_STORED;
    }
}
