package io.zerows.epoch.web.exception;

import io.r2mo.vertx.common.exception.VertxBootException;
import io.zerows.core.constant.em.EmSecure;

/**
 * @author lang : 2025-09-30
 */
public class _40076Exception400WallSize extends VertxBootException {
    public _40076Exception400WallSize(final EmSecure.AuthWall wall, final String size) {
        super(ERR._40076, wall.key(), size);
    }
}
