package io.zerows.extension.runtime.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.extension.runtime.workflow.eon.em.PassWay;

/**
 * @author lang : 2025-09-30
 */
public class _80610Exception409InValidTaskApi extends VertxWebException {
    public _80610Exception409InValidTaskApi(final PassWay input,
                                            final PassWay expected) {
        super(ERR._80610, input, expected);
    }
}
