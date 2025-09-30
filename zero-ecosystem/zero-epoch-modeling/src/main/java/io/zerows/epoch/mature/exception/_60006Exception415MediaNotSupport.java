package io.zerows.epoch.mature.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.core.util.Ut;
import jakarta.ws.rs.core.MediaType;

import java.util.Set;

/**
 * @author lang : 2025-09-30
 */
public class _60006Exception415MediaNotSupport extends VertxWebException {
    public _60006Exception415MediaNotSupport(final MediaType media,
                                             final Set<MediaType> expected) {
        super(ERR._60006, media.toString(), Ut.fromJoin(expected.toArray(new MediaType[]{})));
    }
}
