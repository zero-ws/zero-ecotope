package io.zerows.plugins.security.common;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface CaptchaStub {

    Future<JsonObject> generate();
}
