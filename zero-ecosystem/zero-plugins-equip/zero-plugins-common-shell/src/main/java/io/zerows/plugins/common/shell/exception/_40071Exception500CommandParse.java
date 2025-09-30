package io.zerows.plugins.common.shell.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import org.apache.commons.cli.ParseException;

/**
 * @author lang : 2025-09-30
 */
public class _40071Exception500CommandParse extends VertxBootException {
    public _40071Exception500CommandParse(final String input,
                                          final ParseException error) {
        super(ERR._40071, input, error.getMessage());
    }
}
