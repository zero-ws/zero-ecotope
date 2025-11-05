package io.zerows.extension.module.rbac.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

import java.util.Objects;

/**
 * @author lang : 2025-09-30
 */
public class _80221Exception401MaximumTimes extends VertxWebException {
    public _80221Exception401MaximumTimes(final Integer times,
                                          final Integer seconds) {
        super(ERR._80221, String.valueOf(times), String.valueOf(Objects.requireNonNull(seconds) / 60));
    }
}
