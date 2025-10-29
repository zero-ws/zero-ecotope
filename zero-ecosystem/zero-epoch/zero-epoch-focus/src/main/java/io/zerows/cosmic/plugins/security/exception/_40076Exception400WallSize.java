package io.zerows.cosmic.plugins.security.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.platform.enums.SecurityType;

/**
 * @author lang : 2025-09-30
 */
public class _40076Exception400WallSize extends VertxBootException {
    public _40076Exception400WallSize(final SecurityType wall, final String size) {
        super(ERR._40076, wall.key(), size);
    }
}
