package io.zerows.extension.module.workflow.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _80605Exception409InValidInstance extends VertxWebException {
    public _80605Exception409InValidInstance(final String instanceId) {
        super(ERR._80605, instanceId);
    }
}
