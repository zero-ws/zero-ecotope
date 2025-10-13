package io.zerows.cortex.webflow;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
class WingsBuffer extends WingsBase {
    WingsBuffer(final Vertx vertxRef) {
        super(vertxRef);
    }

    @Override
    public void output(final HttpServerResponse response, final Envelop envelop) {
        /*
         * No freedom support
         *
         * Buffer only ( Buffer output )
         * 1. Content-Type is `application/octet-stream`
         * 2. Replied in Buffer mode
         * byte[] data body instead of String.
         *
         * Situation 1:
         * Download stream here for file download here
         */
        response.end(envelop.outBuffer());
    }
}
