package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80204Exception401PasswordWrong extends VertxWebException {
    public _80204Exception401PasswordWrong(final String username) {
        super(ERR._80204, username);
    }
}
