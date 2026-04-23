package io.zerows.extension.module.ambient.api;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.apps.KDS;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AppActorTest {

    @Test
    void shouldBuildDatabaseResponseWhenHistoryAndWorkflowAreMissing() {
        final JsonObject argument = new JsonObject()
            .put("username", "r2admin")
            .put("password", "secret")
            .put("jdbcUrl", "jdbc:mysql://127.0.0.1:3306/R2_ADMIN_APP");

        final JsonObject response = AppActor.toDatabaseResponse(KDS.of("ambient-test-app"), argument);

        Assertions.assertTrue(response.containsKey("database"));
        Assertions.assertTrue(response.containsKey("history"));
        Assertions.assertTrue(response.containsKey("workflow"));
        Assertions.assertTrue(response.containsKey("argument"));
        Assertions.assertNull(response.getValue("database"));
        Assertions.assertNull(response.getValue("history"));
        Assertions.assertNull(response.getValue("workflow"));
        Assertions.assertFalse(response.getJsonObject("argument").containsKey("username"));
        Assertions.assertFalse(response.getJsonObject("argument").containsKey("password"));
        Assertions.assertEquals(
            "jdbc:mysql://127.0.0.1:3306/R2_ADMIN_APP",
            response.getJsonObject("argument").getString("jdbcUrl")
        );
    }
}
