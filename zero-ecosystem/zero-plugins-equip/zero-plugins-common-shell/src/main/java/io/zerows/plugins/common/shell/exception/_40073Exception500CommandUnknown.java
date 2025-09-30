package io.zerows.plugins.common.shell.exception;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-30
 */
public class _40073Exception500CommandUnknown extends VertxBootException {
    public _40073Exception500CommandUnknown(final String command) {
        super(ERR._40073, command);
    }
}
