package io.zerows.plugins.security.captcha;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface CaptchaStub {

    Future<JsonObject> generate();
}
