package io.zerows.plugins.security.sms;

import io.vertx.core.Future;

public class SmsService implements SmsStub {
    @Override
    public Future<Boolean> sendCaptcha(final String to) {
        return null;
    }
}
