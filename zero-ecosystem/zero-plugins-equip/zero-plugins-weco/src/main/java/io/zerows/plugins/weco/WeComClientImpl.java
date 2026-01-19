package io.zerows.plugins.weco;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;

@Defer
class WeComClientImpl implements WeComClient {

    @Override
    public Future<JsonObject> authUrl(final String redirectUri, final String state) {
        return null;
    }

    @Override
    public Future<JsonObject> login(final String code) {
        return null;
    }

    @Override
    public Future<JsonObject> qrCode(final String state) {
        return null;
    }

    @Override
    public Future<JsonObject> checkStatus(final String uuid) {
        return null;
    }
}
