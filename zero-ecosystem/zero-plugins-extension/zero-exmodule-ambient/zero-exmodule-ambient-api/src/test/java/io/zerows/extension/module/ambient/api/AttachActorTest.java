package io.zerows.extension.module.ambient.api;

import io.r2mo.base.io.modeling.FileRange;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.web.Envelop;
import io.zerows.extension.module.ambient.servicespec.DocRStub;
import io.zerows.extension.skeleton.spi.ExAttachment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

class AttachActorTest {

    @Test
    void shouldKeepRangeHeaderAndPopulateFileSizeOnDownloadResponse() throws Exception {
        final AttachActor actor = new AttachActor();
        final Buffer expected = Buffer.buffer("partial-range");
        setField(actor, "reader", new StubReader(expected));

        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-001")
            .put("Range", "bytes=0-1023")
            .put(KName.SIZE, 4096);
        final Envelop request = Envelop.success(filters);
        request.headers(MultiMap.caseInsensitiveMultiMap().add("Range", "bytes=0-1023"));

        final Envelop response = actor.download(request).toCompletionStage().toCompletableFuture().join();

        Assertions.assertEquals(expected, response.outBuffer());
        Assertions.assertEquals("bytes=0-1023", response.headers().get("Range"));
        Assertions.assertEquals(4096L, ((Number) response.context("X_FILE_SIZE", Object.class)).longValue());
    }

    @Test
    void shouldStillPopulateFileSizeWhenDownloadHasNoRangeHeader() throws Exception {
        final AttachActor actor = new AttachActor();
        final Buffer expected = Buffer.buffer("full-download");
        setField(actor, "reader", new StubReader(expected));

        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-002")
            .put(KName.SIZE, 2048);
        final Envelop request = Envelop.success(filters);

        final Envelop response = actor.download(request).toCompletionStage().toCompletableFuture().join();

        Assertions.assertEquals(expected, response.outBuffer());
        Assertions.assertEquals(2048L, ((Number) response.context("X_FILE_SIZE", Object.class)).longValue());
    }

    @Test
    void shouldLoadFileSizeFromAttachmentMetadataWhenFiltersDoNotContainSize() throws Exception {
        final AttachActor actor = new AttachActor();
        final Buffer expected = Buffer.buffer("metadata-backed");
        setField(actor, "reader", new StubReader(expected));
        setField(actor, "attachment", new StubAttachment(8192));

        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-003")
            .put("Range", "bytes=0-1023");
        final Envelop request = Envelop.success(filters);
        request.headers(MultiMap.caseInsensitiveMultiMap().add("Range", "bytes=0-1023"));

        final Envelop response = actor.download(request).toCompletionStage().toCompletableFuture().join();

        Assertions.assertEquals(expected, response.outBuffer());
        Assertions.assertEquals(8192L, ((Number) response.context("X_FILE_SIZE", Object.class)).longValue());
    }

    private static void setField(final Object target, final String fieldName, final Object value) throws Exception {
        final Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private record StubReader(Buffer buffer) implements DocRStub {
        @Override
        public Future<JsonArray> fetchDoc(final String sigma, final String directoryId) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> fetchTrash(final String sigma) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> searchDoc(final String sigma, final String keyword) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<Buffer> downloadDoc(final String key) {
            return Future.succeededFuture(this.buffer);
        }

        @Override
        public Future<Buffer> downloadDoc(final String key, final FileRange range) {
            return Future.succeededFuture(this.buffer);
        }

        @Override
        public Future<Buffer> downloadDoc(final Set<String> keys) {
            return Future.succeededFuture(this.buffer);
        }
    }

    private record StubAttachment(int size) implements ExAttachment {
        @Override
        public Future<JsonArray> uploadAsync(final JsonArray data) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> uploadAsync(final JsonArray data, final JsonObject params) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> saveAsync(final JsonObject condition, final JsonArray data) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> saveAsync(final JsonObject condition, final JsonArray data, final JsonObject params) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> removeAsync(final JsonObject condition) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> purgeAsync(final JsonArray attachment) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> updateAsync(final JsonArray attachment, final boolean active) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<JsonArray> fetchAsync(final JsonObject condition) {
            return Future.succeededFuture(new JsonArray().add(new JsonObject().put(KName.SIZE, this.size)));
        }

        @Override
        public Future<Buffer> downloadAsync(final Set<String> keys) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<Buffer> downloadAsync(final String key) {
            return Future.failedFuture("Not required in this test");
        }

        @Override
        public Future<Buffer> downloadAsync(final String key, final FileRange range) {
            return Future.failedFuture("Not required in this test");
        }
    }
}
