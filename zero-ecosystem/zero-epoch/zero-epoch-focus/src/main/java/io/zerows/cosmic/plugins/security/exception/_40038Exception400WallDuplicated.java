package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.support.Ut;

import java.util.Set;

/**
 * @author lang : 2025-09-30
 */
public class _40038Exception400WallDuplicated extends VertxBootException {
    public _40038Exception400WallDuplicated(final Set<String> classNames) {
        super(ERR._40038, Ut.fromJoin(classNames));
    }
}
