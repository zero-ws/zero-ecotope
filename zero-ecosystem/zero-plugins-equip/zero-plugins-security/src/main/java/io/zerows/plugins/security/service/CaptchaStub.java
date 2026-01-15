package io.zerows.plugins.security.service;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface CaptchaStub {

    Future<JsonObject> generate();

    Future<Boolean> validate(String captchaId, String captcha);
}
