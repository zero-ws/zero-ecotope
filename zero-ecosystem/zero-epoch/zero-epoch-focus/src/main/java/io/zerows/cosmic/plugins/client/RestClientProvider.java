package io.zerows.cosmic.plugins.client;

import io.zerows.sdk.plugins.AddOn;
import io.zerows.sdk.plugins.AddOnProvider;

class RestClientProvider extends AddOnProvider<RestClient> {
    RestClientProvider(final AddOn<RestClient> addOn) {
        super(addOn);
    }
}
