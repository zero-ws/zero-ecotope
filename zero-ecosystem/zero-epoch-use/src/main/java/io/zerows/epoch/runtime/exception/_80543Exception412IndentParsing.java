package io.zerows.epoch.runtime.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80543Exception412IndentParsing extends VertxWebException {
    public _80543Exception412IndentParsing(final String targetIndent,
                                           final String configStr) {
        super(ERR._80543, targetIndent, configStr);
    }
}
