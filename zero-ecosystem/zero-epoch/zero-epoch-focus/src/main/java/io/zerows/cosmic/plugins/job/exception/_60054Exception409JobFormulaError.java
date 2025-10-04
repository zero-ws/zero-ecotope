package io.zerows.cosmic.plugins.job.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60054Exception409JobFormulaError extends VertxWebException {

    public _60054Exception409JobFormulaError(final String formula) {
        super(ERR._60054, formula);
    }
}
