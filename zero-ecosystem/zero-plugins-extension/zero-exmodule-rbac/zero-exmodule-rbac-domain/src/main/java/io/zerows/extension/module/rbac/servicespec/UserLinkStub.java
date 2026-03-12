package io.zerows.extension.module.rbac.servicespec;

import io.r2mo.jaas.element.MSGroup;
import io.r2mo.jaas.element.MSRole;
import io.vertx.core.Future;
import io.zerows.program.Ux;

import java.util.List;

public interface UserLinkStub {

    Future<List<MSRole>> rolesByUser(String userId);

    default Future<List<MSRole>> rolesByGroup(final String groupId) {
        return Ux.futureL();
    }

    default Future<List<MSGroup>> groupsByUser(final String userId) {
        return Ux.futureL();
    }
}
