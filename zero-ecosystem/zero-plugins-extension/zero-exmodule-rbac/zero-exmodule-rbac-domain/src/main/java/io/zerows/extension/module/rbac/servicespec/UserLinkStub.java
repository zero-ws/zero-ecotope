package io.zerows.extension.module.rbac.servicespec;

import io.r2mo.jaas.element.MSGroup;
import io.r2mo.jaas.element.MSRole;
import io.vertx.core.Future;

import java.util.List;

public interface UserLinkStub {

    Future<List<MSRole>> rolesByUser(String userId);

    Future<List<MSRole>> rolesByGroup(String groupId);

    Future<List<MSGroup>> groupsByUser(String userId);
}
