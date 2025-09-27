package io.zerows.extension.commerce.rbac.agent.service.role;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

public interface RoleStub {

    Future<JsonObject> roleSave(JsonObject data, User user);
}
