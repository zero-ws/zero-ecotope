package io.zerows.sdk.security;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

/**
 * 安全墙的执行类，替换旧版的几个注解的方式，使用接口约定
 *
 * @author lang : 2025-10-29
 */
public interface WallExecutor {

    Future<Boolean> authenticate(JsonObject credentials);

    Future<JsonObject> authorize(User user);

    Future<JsonObject> resource(JsonObject params);
}
