package io.zerows.plugins.email;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

class EmailProvider extends AddOnProvider<EmailClient> {
    EmailProvider(final AddOn<EmailClient> addOn) {
        super(addOn);
    }
}

