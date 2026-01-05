package io.zerows.cosmic.bootstrap;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import io.zerows.cortex.metadata.WebRequest;
import io.zerows.cosmic.exception._60052Exception411ContentLength;

class SentryPreUpload implements Sentry.Pre {
    @Override
    public void verify(final RoutingContext context, final WebRequest wrapRequest, final Object[] parsed) {
        final HttpServerRequest request = context.request();
        if (!request.isExpectMultipart()) {
            // 非上传请求，跳过
            return;
        }

        if (request.headers().contains(HttpHeaders.CONTENT_LENGTH)) {
            // 上传请求必须带有长度
            return;
        }

        // Content-Length = 0
        throw new _60052Exception411ContentLength(0);
    }
}
