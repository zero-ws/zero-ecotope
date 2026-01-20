package io.zerows.cosmic.plugins.client;

import io.r2mo.typed.cc.Cc;
import io.zerows.sdk.plugins.AddOnManager;

class RestClientManager extends AddOnManager<RestClient> {
    private static final Cc<String, RestClient> CC_STORED = Cc.open();

    private static final RestClientManager INSTANCE = new RestClientManager();

    private RestClientManager() {
    }

    static RestClientManager of() {
        return INSTANCE;
    }

    @Override
    protected Cc<String, RestClient> stored() {
        return CC_STORED;
    }
}
