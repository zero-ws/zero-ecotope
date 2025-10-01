package io.zerows.epoch.based.exception;

import io.r2mo.vertx.common.exception.VertxWebException;

/**
 * @author lang : 2025-09-30
 */
public class _60040Exception412ContractField extends VertxWebException {
    public _60040Exception412ContractField(final Class<?> fieldType,
                                           final Class<?> target,
                                           final Integer times) {
        super(ERR._60040, fieldType.getSimpleName(), target.getName(), times);
    }
}
