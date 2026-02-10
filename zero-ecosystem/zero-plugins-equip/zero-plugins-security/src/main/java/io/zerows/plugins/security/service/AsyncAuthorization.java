package io.zerows.plugins.security.service;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.program.Ux;

public interface AsyncAuthorization {

    Cc<String, AsyncAuthorization> CC_SKELETON = Cc.openThread();

    static AsyncAuthorization of(final String appId) {
        // Fix: [ XMOD ] 授权组件为空 / resource
        return CC_SKELETON.pick(() -> Ux.waitService(AsyncAuthorization.class), appId);
    }

    static AsyncAuthorization of() {
        return CC_SKELETON.pick(() -> Ux.waitService(AsyncAuthorization.class));
    }

    Future<JsonObject> seekResource(JsonObject params);

    Future<JsonObject> seekProfile(User user);
}
