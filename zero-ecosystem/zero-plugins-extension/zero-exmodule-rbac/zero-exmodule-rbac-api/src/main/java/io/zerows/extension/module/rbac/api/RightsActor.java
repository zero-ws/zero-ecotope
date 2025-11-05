package io.zerows.extension.module.rbac.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.zerows.epoch.annotations.Address;
import io.zerows.epoch.annotations.Queue;
import io.zerows.extension.module.rbac.servicespec.GroupStub;
import io.zerows.extension.module.rbac.servicespec.RightsStub;
import jakarta.inject.Inject;

@Queue
public class RightsActor {

    @Inject
    private transient GroupStub groupStub;
    @Inject
    private transient RightsStub setStub;

    @Address(Addr.Group.GROUP_SIGMA)
    public Future<JsonArray> fetchGroups(final String sigma) {
        return this.groupStub.fetchGroups(sigma);
    }

    @Address(Addr.Role.ROLE_PERM_UPDATE)
    public Future<JsonArray> updateRolePerm(final String roleId, final JsonArray data) {
        return this.setStub.saveRoles(roleId, data);
    }
}
