package io.zerows.plugins.weco;

import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Defer;

@Defer
class WeChatClientImpl implements WeChatClient {

    @Override
    public Future<JsonObject> authUrl(final String redirectUri, final String state) {
        return null;
    }

    @Override
    public Future<JsonObject> login(final String code) {
        return null;
    }

    @Override
    public Future<JsonObject> qrCode() {
        return null;
    }

    @Override
    public Future<JsonObject> checkStatus(final String uuid) {
        return null;
    }

    @Override
    public Future<Boolean> checkEcho(final WeArgsSignature params) {
        return null;
    }

    @Override
    public Future<JsonObject> extractUser(final WeArgsCallback callback) {
        return null;
    }
}
