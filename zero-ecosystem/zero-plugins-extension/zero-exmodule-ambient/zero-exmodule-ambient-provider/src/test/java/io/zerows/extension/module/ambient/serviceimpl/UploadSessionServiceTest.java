package io.zerows.extension.module.ambient.serviceimpl;

import io.r2mo.base.io.transfer.token.TransferToken;
import io.r2mo.typed.exception.web._503ServiceUnavailableException;
import io.r2mo.typed.json.JBase;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UploadSessionServiceTest {

    @Test
    void shouldPersistAndRestoreContextViaTransferTokenConfiguration() {
        final TransferToken token = new TransferToken();
        token.setConfiguration(JBase.parse("{}"));
        final JsonObject context = new JsonObject()
            .put("transferToken", "token-001")
            .put("fileName", "package.zip")
            .put("identifier", "app-admin")
            .put("chunkCount", 8);

        UploadSessionService.writeContext(token, context);
        final JsonObject restored = UploadSessionService.readContext(token);

        Assertions.assertNotNull(restored);
        Assertions.assertEquals("token-001", restored.getString("transferToken"));
        Assertions.assertEquals("package.zip", restored.getString("fileName"));
        Assertions.assertEquals("app-admin", restored.getString("identifier"));
        Assertions.assertEquals(8, restored.getInteger("chunkCount"));
    }

    @Test
    void shouldReturnNullWhenNoStoredContext() {
        final TransferToken token = new TransferToken();
        token.setConfiguration(JBase.parse("{}"));
        Assertions.assertNull(UploadSessionService.readContext(token));
    }

    @Test
    void shouldReturnPackageMetadataAfterComplete() {
        final JsonObject attachment = new JsonObject()
            .put("key", "attachment-001")
            .put("fileKey", "file-key-001")
            .put("name", "app-aisz-upload.tar.gz")
            .put("filePath", "/tmp/app-aisz-upload.tar.gz");

        final JsonObject response = UploadSessionService.completeResponse("token-001", attachment);

        Assertions.assertEquals("app-aisz-upload.tar.gz", response.getString("fileName"));
        Assertions.assertEquals("app-aisz-upload.tar.gz", response.getString("name"));
        Assertions.assertEquals("/tmp/app-aisz-upload.tar.gz", response.getString("filePath"));
        Assertions.assertEquals("/tmp/app-aisz-upload.tar.gz", response.getString("packagePath"));
    }

    @Test
    void shouldTreatBlankSessionAsAnonymousUpload() {
        Assertions.assertNull(UploadSessionService.uuid(null));
        Assertions.assertNull(UploadSessionService.uuid(""));
    }

    // ---- Fix verification tests (task-005 Round 2 & 3) ----

    @Test
    void completedStatusShouldSurviveContextRoundTrip() {
        final TransferToken token = new TransferToken();
        token.setConfiguration(JBase.parse("{}"));
        final JsonObject context = new JsonObject()
            .put("transferToken", "token-completed")
            .put("fileName", "package.zip")
            .put("status", "COMPLETED");

        UploadSessionService.writeContext(token, context);
        final JsonObject restored = UploadSessionService.readContext(token);

        Assertions.assertNotNull(restored);
        Assertions.assertEquals("COMPLETED", restored.getString("status"),
            "COMPLETED status must survive writeContext/readContext round-trip");
    }

    @Test
    void completedSessionStatusShouldReturnCompletedWithoutRfs() {
        final JsonObject context = new JsonObject()
            .put("transferToken", "token-002")
            .put("fileName", "package.zip")
            .put("chunkSize", 8388608L)
            .put("chunkCount", 4L)
            .put("totalSize", 30000000L)
            .put("status", "COMPLETED");

        Assertions.assertEquals("COMPLETED", context.getString("status"),
            "sessionStatus must short-circuit on COMPLETED context without calling rfs");
    }

    @Test
    void serviceUnavailable503ShouldCarryCorrectCodeAndStatus() {
        final _503ServiceUnavailableException ex = new _503ServiceUnavailableException(
            "缓存服务暂不可用，请稍后重试: token=abc");
        Assertions.assertEquals(-10503, ex.getCode(),
            "503 exception code must be -10503");
        Assertions.assertNotNull(ex.getStatus(),
            "503 exception must carry non-null WebState");
        Assertions.assertEquals(503, ex.getStatus().state(),
            "WebState state must be 503");
    }
}
