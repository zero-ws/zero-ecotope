package io.zerows.plugins.security.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.common.CaptchaStub;
import jakarta.inject.Inject;

@Queue
public class LoginActor {

    @Inject
    private CaptchaStub captchaStub;

    @Address(Addr.API_AUTH_LOGIN)
    public Future<JsonObject> login(final JsonObject captcha) {
        return Future.succeededFuture();
    }

    @Address(Addr.API_AUTH_CAPTCHA)
    public Future<JsonObject> captcha(final JsonObject captcha) {
        System.out.println(captcha);
        return this.captchaStub.generate();
    }
}
