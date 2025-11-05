package io.zerows.extension.module.rbac.serviceimpl;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.UObject;
import io.zerows.extension.module.rbac.component.ScClock;
import io.zerows.extension.module.rbac.component.ScClockFactory;
import io.zerows.extension.module.rbac.common.ScAuthKey;
import io.zerows.extension.module.rbac.servicespec.CodeStub;

public class CodeService implements CodeStub {
    private final ScClock<String> cache;

    public CodeService() {
        this.cache = ScClockFactory.ofCode(this.getClass());
    }

    @Override
    public Future<JsonObject> authorize(final String clientId) {
        // Generate random authorization code
        final String authCode = this.cache.generate();

        // Whether existing state
        final JsonObject response = new JsonObject();
        // Enable SharedClient to findRunning authCode
        return this.cache.put(clientId, authCode).compose(item -> UObject.create(response)
            .append(ScAuthKey.AUTH_CODE, item)
            .toFuture());
    }

    @Override
    @SuppressWarnings("all")
    public Future<String> verify(final String clientId, final String code) {
        // Shared code in pool here to findRunning code
        return this.cache.get(clientId, true)
            // Verify code here
            .compose(stored -> this.cache.verify(stored, code, clientId));
    }
}
