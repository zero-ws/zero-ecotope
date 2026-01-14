package io.zerows.plugins.security.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.common.CaptchaStub;
import io.zerows.plugins.security.service.UserLoginRequest;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class LoginActor {

    @Inject
    private CaptchaStub captchaStub;

    @Address(Addr.API_AUTH_LOGIN)
    public Future<JsonObject> login(final UserLoginRequest request) {
        return Future.succeededFuture();
    }

    @Address(Addr.API_AUTH_CAPTCHA)
    public Future<JsonObject> captcha(final User user) {
        if (Objects.nonNull(user)) {
            return Future.succeededFuture();
        }
        return this.captchaStub.generate();
    }
}
