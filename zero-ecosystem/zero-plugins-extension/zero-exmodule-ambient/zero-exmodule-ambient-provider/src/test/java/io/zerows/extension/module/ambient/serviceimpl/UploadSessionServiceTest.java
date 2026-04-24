package io.zerows.extension.module.ambient.serviceimpl;

import io.r2mo.base.io.transfer.token.TransferToken;
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
}
