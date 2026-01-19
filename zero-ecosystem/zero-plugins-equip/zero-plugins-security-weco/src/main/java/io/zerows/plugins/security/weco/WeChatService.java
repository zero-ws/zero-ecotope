package io.zerows.plugins.security.weco;

import io.r2mo.xync.weco.wechat.WeArgsCallback;
import io.r2mo.xync.weco.wechat.WeArgsSignature;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class WeChatService implements WeChatStub {
    @Override
    public Future<JsonObject> getAuthUrl(final String redirectUri, final String state) {
        return null;
    }

    @Override
    public Future<WeChatReqPreLogin> validate(final WeChatReqPreLogin request) {
        return null;
    }

    @Override
    public Future<JsonObject> getQrCode() {
        return null;
    }

    @Override
    public Future<JsonObject> checkStatus(final String uuid) {
        return null;
    }

    @Override
    public boolean checkEcho(final WeArgsSignature params) {
        return false;
    }

    @Override
    public Future<JsonObject> extract(final String uuid, final WeArgsCallback parameter) {
        return null;
    }
}
