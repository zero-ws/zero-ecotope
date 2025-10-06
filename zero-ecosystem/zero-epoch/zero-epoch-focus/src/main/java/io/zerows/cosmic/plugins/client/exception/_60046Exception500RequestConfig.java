package io.zerows.cosmic.plugins.client.exception;

import io.r2mo.vertx.common.exception.VertxWebException;
import io.zerows.platform.metadata.KIntegration;

/**
 * @author lang : 2025-09-30
 */
public class _60046Exception500RequestConfig extends VertxWebException {
    public _60046Exception500RequestConfig(final KIntegration.Api request,
                                           final String config) {
        super(ERR._60046, request.toString(), config);
    }
}
