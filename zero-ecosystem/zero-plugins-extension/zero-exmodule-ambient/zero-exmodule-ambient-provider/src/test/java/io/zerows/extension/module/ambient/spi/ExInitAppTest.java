package io.zerows.extension.module.ambient.spi;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExInitAppTest {

    @Test
    void shouldKeepSourceFromRequestWhenPersistedXAppHasNoSourceField() {
        final ExInitApp init = new ExInitApp();
        final JsonObject source = new JsonObject()
            .put("category", "MYSQL_8")
            .put("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/R2_ADMIN_APP");
        final JsonObject request = new JsonObject()
            .put(KName.KEY, "aisz-app-hotel")
            .put(KName.NAME, "aisz-app-hotel")
            .put(KName.SOURCE, source.copy());
        final JsonObject persisted = new JsonObject()
            .put(KName.KEY, "aisz-app-hotel")
            .put(KName.NAME, "aisz-app-hotel");

        final JsonObject result = init.result(persisted, request);

        Assertions.assertEquals(source, result.getJsonObject(KName.SOURCE));
    }
}
