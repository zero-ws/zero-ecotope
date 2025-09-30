package io.zerows.plugins.office.excel.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60038Exception404ConnectMissing extends VertxWebException {
    public _60038Exception404ConnectMissing(final String table) {
        super(ERR._60038, table);
    }
}
