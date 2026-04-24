package io.zerows.cortex.webflow;

import io.r2mo.base.io.modeling.FileRange;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;

import java.util.Objects;

class WingsBuffer extends WingsBase {
    WingsBuffer(final Vertx vertxRef) {
        super(vertxRef);
    }

    @Override
    public void output(final HttpServerResponse response, final Envelop envelop) {
        response.putHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        final String rangeHeader = null == envelop.headers() ? null : envelop.headers().get("Range");
        final FileRange range = FileRange.of(rangeHeader);
        if (Objects.nonNull(range)) {
            final long totalSize = this.extractFileSize(envelop);
            response.setStatusCode(206);
            response.setStatusMessage("Partial Content");
            response.putHeader(HttpHeaders.CONTENT_RANGE, this.contentRange(range, totalSize));
        }
        response.end(envelop.outBuffer());
    }

    String contentRange(final FileRange range, final long totalSize) {
        final long end = null != range.getEnd() ? range.getEnd() : (totalSize > 0 ? totalSize - 1 : 0);
        return "bytes " + range.getStart() + "-" + end + (totalSize > 0 ? "/" + totalSize : "/*");
    }

    private long extractFileSize(final Envelop envelop) {
        final Object fileSize = envelop.context("X_FILE_SIZE", Object.class);
        if (fileSize instanceof Number) {
            return ((Number) fileSize).longValue();
        }
        return -1;
    }
}
