package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80226Exception404AdmitDaoNull extends VertxWebException {
    public _80226Exception404AdmitDaoNull(final String daoStr) {
        super(ERR._80226, daoStr);
    }
}
