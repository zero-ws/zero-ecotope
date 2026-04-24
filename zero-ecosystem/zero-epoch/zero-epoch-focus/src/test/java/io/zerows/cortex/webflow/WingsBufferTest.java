package io.zerows.cortex.webflow;

import io.r2mo.base.io.modeling.FileRange;
import io.vertx.core.MultiMap;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.zerows.epoch.web.Envelop;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class WingsBufferTest {

    @Test
    public void shouldFormatContentRangeWithKnownTotalForOpenEndedRange() {
        final FileRange range = FileRange.of("bytes=1024-");

        final String contentRange = new WingsBuffer(null).contentRange(range, 4096L);

        Assert.assertEquals("bytes 1024-4095/4096", contentRange);
    }

    @Test
    public void shouldFormatContentRangeWithWildcardTotalWhenFileSizeIsMissing() {
        final FileRange range = FileRange.of("bytes=0-1023");

        final String contentRange = new WingsBuffer(null).contentRange(range, -1L);

        Assert.assertEquals("bytes 0-1023/*", contentRange);
    }

    @Test
    public void shouldExtractKnownFileSizeFromEnvelopContext() {
        final Envelop envelop = Envelop.success(Buffer.buffer("partial"));
        final Map<String, Object> context = new HashMap<>();
        context.put("X_FILE_SIZE", 4096L);
        envelop.content(context);
        envelop.headers(MultiMap.caseInsensitiveMultiMap().add("Range", "bytes=0-1023"));

        final long fileSize = new TestingWingsBuffer().extract(envelop);

        Assert.assertEquals(4096L, fileSize);
    }

    @Test
    public void shouldBuildRealContentRangeFromEnvelopContextForPartialDownload() {
        final Envelop envelop = Envelop.success(Buffer.buffer("partial"));
        final Map<String, Object> context = new HashMap<>();
        context.put("X_FILE_SIZE", 4096L);
        envelop.content(context);
        envelop.headers(MultiMap.caseInsensitiveMultiMap().add("Range", "bytes=0-1023"));

        final TestingWingsBuffer wings = new TestingWingsBuffer();
        final FileRange range = FileRange.of(envelop.headers().get("Range"));
        final String contentRange = wings.contentRange(range, wings.extract(envelop));

        Assert.assertEquals("bytes 0-1023/4096", contentRange);
    }

    @Test
    public void shouldOutputPartialContentHeadersForRangeDownload() {
        final Envelop envelop = Envelop.success(Buffer.buffer("partial-body"));
        final Map<String, Object> context = new HashMap<>();
        context.put("X_FILE_SIZE", 4096L);
        envelop.content(context);
        envelop.headers(MultiMap.caseInsensitiveMultiMap().add("Range", "bytes=0-1023"));

        final CapturedResponse captured = new CapturedResponse();
        new WingsBuffer(null).output(captured.response(), envelop);

        Assert.assertEquals(Integer.valueOf(206), captured.statusCode);
        Assert.assertEquals("Partial Content", captured.statusMessage);
        Assert.assertEquals("bytes", captured.headers.get("accept-ranges"));
        Assert.assertEquals("bytes 0-1023/4096", captured.headers.get("content-range"));
        Assert.assertEquals(Buffer.buffer("partial-body"), captured.body);
    }

    @Test
    public void shouldPreserveNormalDownloadWithoutContentRangeWhenRangeMissing() {
        final Envelop envelop = Envelop.success(Buffer.buffer("full-body"));

        final CapturedResponse captured = new CapturedResponse();
        new WingsBuffer(null).output(captured.response(), envelop);

        Assert.assertNull(captured.statusCode);
        Assert.assertNull(captured.statusMessage);
        Assert.assertEquals("bytes", captured.headers.get("accept-ranges"));
        Assert.assertNull(captured.headers.get("content-range"));
        Assert.assertEquals(Buffer.buffer("full-body"), captured.body);
    }

    private static class TestingWingsBuffer extends WingsBuffer {
        TestingWingsBuffer() {
            super(null);
        }

        long extract(final Envelop envelop) {
            final Object fileSize = envelop.context("X_FILE_SIZE", Object.class);
            if (fileSize instanceof Number) {
                return ((Number) fileSize).longValue();
            }
            return -1L;
        }
    }

    private static class CapturedResponse {
        private final Map<String, String> headers = new HashMap<>();
        private Integer statusCode;
        private String statusMessage;
        private Buffer body;
        private final HttpServerResponse response = (HttpServerResponse) Proxy.newProxyInstance(
            HttpServerResponse.class.getClassLoader(),
            new Class<?>[]{HttpServerResponse.class},
            (proxy, method, args) -> {
                final String name = method.getName();
                if ("putHeader".equals(name) && null != args && args.length == 2) {
                    this.headers.put(String.valueOf(args[0]).toLowerCase(), String.valueOf(args[1]));
                    return proxy;
                }
                if ("setStatusCode".equals(name) && null != args && args.length == 1) {
                    this.statusCode = (Integer) args[0];
                    return proxy;
                }
                if ("setStatusMessage".equals(name) && null != args && args.length == 1) {
                    this.statusMessage = (String) args[0];
                    return proxy;
                }
                if ("end".equals(name)) {
                    if (null != args && 0 < args.length && args[0] instanceof Buffer) {
                        this.body = (Buffer) args[0];
                    }
                    return Future.succeededFuture();
                }
                if (HttpServerResponse.class.isAssignableFrom(method.getReturnType())) {
                    return proxy;
                }
                if (Future.class.isAssignableFrom(method.getReturnType())) {
                    return Future.succeededFuture();
                }
                if (boolean.class == method.getReturnType()) {
                    return false;
                }
                if (int.class == method.getReturnType()) {
                    return 0;
                }
                if (long.class == method.getReturnType()) {
                    return 0L;
                }
                return null;
            });

        HttpServerResponse response() {
            return this.response;
        }
    }
}
