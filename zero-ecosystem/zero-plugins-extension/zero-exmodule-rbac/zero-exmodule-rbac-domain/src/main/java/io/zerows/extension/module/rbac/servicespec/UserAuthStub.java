package io.zerows.extension.module.rbac.servicespec;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.typed.enums.TypeLogin;
import io.vertx.core.Future;

public interface UserAuthStub {
    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    Future<MSUser> whereUsername(String username);

    Future<MSUser> whereBy(String id, TypeLogin typeID);
}
