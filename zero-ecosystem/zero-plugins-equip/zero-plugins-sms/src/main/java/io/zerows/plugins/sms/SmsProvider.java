package io.zerows.plugins.sms;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

public class SmsProvider extends AddOnProvider<SmsClient> {
    protected SmsProvider(AddOn<SmsClient> addOn) {
        super(addOn);
    }
}