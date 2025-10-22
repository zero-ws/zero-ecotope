package io.zerows.extension.commerce.rbac.agent.service.login.pre;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.metadata.UObject;
import io.zerows.extension.commerce.rbac.eon.AuthKey;
import io.zerows.extension.commerce.rbac.uca.timer.ClockFactory;
import io.zerows.extension.commerce.rbac.uca.timer.ScClock;

public class CodeService implements CodeStub {
    private final ScClock<String> cache;

    public CodeService() {
        this.cache = ClockFactory.ofCode(this.getClass());
    }

    @Override
    public Future<JsonObject> authorize(final String clientId) {
        // Generate random authorization code
        final String authCode = this.cache.generate();

        // Whether existing state
        final JsonObject response = new JsonObject();
        // Enable SharedClient to findRunning authCode
        return this.cache.put(clientId, authCode).compose(item -> UObject.create(response)
            .append(AuthKey.AUTH_CODE, item)
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
