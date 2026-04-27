package io.zerows.plugins.excel.metadata;

import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExRecordTest {

    @Test
    void shouldNotOverrideExistingValueWithDefault() {
        final ExRecord record = new ExRecord(null);
        record.put("password", "$sha512$source");

        record.putOr(new JsonObject().put("password", "$sha512$default"));

        Assertions.assertEquals("$sha512$source", record.get("password"));
    }

    @Test
    void shouldFillDefaultWhenValueIsMissingOrBlank() {
        final ExRecord missing = new ExRecord(null);
        missing.putOr(new JsonObject().put("password", "$sha512$default"));
        Assertions.assertEquals("$sha512$default", missing.get("password"));

        final ExRecord blank = new ExRecord(null);
        blank.put("password", "");
        blank.putOr(new JsonObject().put("password", "$sha512$default"));
        Assertions.assertEquals("$sha512$default", blank.get("password"));
    }
}
