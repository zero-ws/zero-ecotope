package io.zerows.extension.module.ambient.spi;

import io.r2mo.base.dbe.Database;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExInitDatabaseTest {

    @Test
    void shouldSerializeDatabaseWithoutCallingRecursiveDatabaseSerializer() {
        final Database database = Database.createDatabase(new JsonObject()
            .put("category", "MYSQL_8")
            .put("hostname", "127.0.0.1")
            .put("instance", "R2_ADMIN_APP")
            .put("port", 3306)
            .put("url", "jdbc:mysql://127.0.0.1:3306/R2_ADMIN_APP")
            .put("username", "r2admin")
            .put("password", "secret")
            .put("driver-class-name", "com.mysql.cj.jdbc.Driver")
            .put("options", new JsonObject().put("ssl", false)));

        final JsonObject result = ExInitDatabase.toDatabaseJson(database);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("MYSQL_8", result.getString("category"));
        Assertions.assertEquals("127.0.0.1", result.getString("hostname"));
        Assertions.assertEquals("R2_ADMIN_APP", result.getString("instance"));
        Assertions.assertEquals(3306, result.getInteger("port"));
        Assertions.assertEquals("jdbc:mysql://127.0.0.1:3306/R2_ADMIN_APP", result.getString("url"));
        Assertions.assertEquals("r2admin", result.getString("username"));
        Assertions.assertEquals("secret", result.getString("password"));
        Assertions.assertEquals("com.mysql.cj.jdbc.Driver", result.getString("driver-class-name"));
        Assertions.assertEquals(Boolean.FALSE, result.getJsonObject("options").getBoolean("ssl"));
    }

    @Test
    void shouldAllowNullDatabaseResultForLoggingPath() {
        final JsonObject input = new JsonObject().put("key", "aisz-app-hotel");

        final JsonObject result = new ExInitDatabase().result(input, null);

        Assertions.assertSame(input, result);
    }
}
