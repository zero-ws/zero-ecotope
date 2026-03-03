package io.zerows.extension.module.rbac.servicespec;

import io.vertx.core.Future;

public interface LoginStub {

    /*
     * Logout workflow
     * {
     *      "user": "uid",
     *      "habitus": "session key"
     * }
     */
    Future<Boolean> logout(final String user, final String habitus);
}
