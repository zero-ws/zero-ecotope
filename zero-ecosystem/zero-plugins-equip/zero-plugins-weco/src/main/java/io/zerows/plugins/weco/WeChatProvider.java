package io.zerows.plugins.weco;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

class WeChatProvider extends AddOnProvider<WeChatClient> {
    protected WeChatProvider(final AddOn<WeChatClient> addOn) {
        super(addOn);
    }
}
