package io.zerows.plugins.security.captcha;

import io.r2mo.function.Fn;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.zerows.plugins.security.SecurityActor;
import io.zerows.plugins.security.SecurityCaptcha;
import io.zerows.plugins.security.exception._80212Exception500CaptchaDisabled;

import java.util.Objects;

public class CaptchaService implements CaptchaStub {
    private static final SecurityCaptcha captcha = SecurityActor.configCaptcha();

    @Override
    public Future<JsonObject> generate() {
        // 异常抛出
        Fn.jvmKo(Objects.isNull(captcha), _80212Exception500CaptchaDisabled.class);

        return Future.succeededFuture();
    }
}
