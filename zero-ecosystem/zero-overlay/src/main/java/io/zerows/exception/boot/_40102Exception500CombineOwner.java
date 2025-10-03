package io.zerows.exception.boot;

import io.r2mo.vertx.common.exception.VertxBootException;

/**
 * @author lang : 2025-09-26
 */
public class _40102Exception500CombineOwner extends VertxBootException {
    public _40102Exception500CombineOwner(final String targetId, final String owner) {
        super(ERR._40102, targetId, owner);
    }
}
