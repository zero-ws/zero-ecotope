package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.rbac.servicespec.RoleStub;
import jakarta.inject.Inject;

@Queue
public class RoleAgent {
    @Inject
    private transient RoleStub stub;

    @Address(Addr.Role.ROLE_SAVE)
    public Future<JsonObject> saveRole(final JsonObject roleData, final User user) {
        return this.stub.roleSave(roleData, user);
    }
}
