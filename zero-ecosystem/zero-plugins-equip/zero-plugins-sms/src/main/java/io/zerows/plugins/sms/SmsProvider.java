package io.zerows.plugins.sms;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

class SmsProvider extends AddOnProvider<SmsClient> {
    protected SmsProvider(final AddOn<SmsClient> addOn) {
        super(addOn);
    }
}