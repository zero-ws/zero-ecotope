package io.zerows.plugins.weco;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

class WeComProvider extends AddOnProvider<WeComClient> {
    protected WeComProvider(final AddOn<WeComClient> addOn) {
        super(addOn);
    }
}
