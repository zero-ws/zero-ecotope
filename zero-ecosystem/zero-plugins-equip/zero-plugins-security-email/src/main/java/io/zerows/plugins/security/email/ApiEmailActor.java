package io.zerows.plugins.security.email;

import io.r2mo.base.util.R2MO;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.plugins.security.email.exception._80351Exception400EmailRequired;
import io.zerows.plugins.security.email.exception._80352Exception400EmailFormat;
import io.zerows.plugins.security.email.exception._80353Exception500EmailSending;
import io.zerows.plugins.security.service.AuthLoginStub;
import io.zerows.plugins.security.service.CaptchaStub;
import io.zerows.program.Ux;
import io.zerows.support.Fx;
import jakarta.inject.Inject;

import java.util.Objects;

@Queue
public class ApiEmailActor {
    @Inject
    private CaptchaStub captchaStub;

    @Inject
    private AuthLoginStub loginStub;

    @Inject
    private EmailStub emailStub;

    @Address(ApiAddr.API_AUTH_EMAIL_SEND)
    public Future<Boolean> sendAsync(final EmailLoginRequest request) {
        // email 非空 / 格式校验
        final String email = request.getEmail();
        if (Objects.isNull(email)) {
            return Fx.failOut(_80351Exception400EmailRequired.class);
        }
        if (!R2MO.isEmail(email)) {
            return Fx.failOut(_80352Exception400EmailFormat.class, email);
        }
        return this.emailStub.sendCaptcha(email).compose(sent -> {
            if (!sent) {
                return Fx.failOut(_80353Exception500EmailSending.class, email);
            }
            return Ux.futureT();
        });
    }

    @Address(ApiAddr.API_AUTH_EMAIL_LOGIN)
    public Future<JsonObject> login(final JsonObject body) {

        return Ux.futureJ();
    }
}
