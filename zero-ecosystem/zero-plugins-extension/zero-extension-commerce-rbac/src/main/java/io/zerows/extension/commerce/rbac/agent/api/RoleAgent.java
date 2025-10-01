package io.zerows.extension.commerce.rbac.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.commerce.rbac.agent.service.role.RoleStub;
import io.zerows.extension.commerce.rbac.eon.Addr;
import jakarta.inject.Inject;

@Queue
public class RoleAgent {
    @Inject
    private transient RoleStub stub;
    @Address(Addr.Role.ROLE_SAVE)
    public Future<JsonObject> saveRole(final JsonObject roleData, final User user) {
        return stub.roleSave(roleData,user);
    }
}
