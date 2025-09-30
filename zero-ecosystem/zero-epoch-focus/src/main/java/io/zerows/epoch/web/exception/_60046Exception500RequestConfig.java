package io.zerows.epoch.web.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.common.app.KIntegrationApi;

/**
 * @author lang : 2025-09-30
 */
public class _60046Exception500RequestConfig extends VertxWebException {
    public _60046Exception500RequestConfig(final KIntegrationApi request,
                                           final String config) {
        super(ERR._60046, request.toString(), config);
    }
}
