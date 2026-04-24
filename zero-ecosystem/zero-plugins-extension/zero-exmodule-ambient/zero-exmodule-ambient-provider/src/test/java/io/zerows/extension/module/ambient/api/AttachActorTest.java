package io.zerows.extension.module.ambient.api;

import io.r2mo.base.io.modeling.FileRange;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AttachActorTest {

    @Test
    void shouldParseRangeHeaderFromFilters() {
        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-001")
            .put("Range", "bytes=0-1023");
        final String rangeHeader = filters.getString("Range");
        final FileRange range = FileRange.of(rangeHeader);
        Assertions.assertNotNull(range);
        Assertions.assertEquals(0L, range.getStart());
        Assertions.assertEquals(1023L, range.getEnd());
    }

    @Test
    void shouldReturnNullRangeWhenHeaderMissing() {
        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-001");
        final String rangeHeader = filters.getString("Range");
        final FileRange range = FileRange.of(rangeHeader);
        Assertions.assertNull(range);
    }

    @Test
    void shouldReturnNullRangeWhenHeaderInvalid() {
        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-001")
            .put("Range", "invalid-range");
        final String rangeHeader = filters.getString("Range");
        final FileRange range = FileRange.of(rangeHeader);
        Assertions.assertNull(range);
    }

    @Test
    void shouldParseOpenEndedRange() {
        final JsonObject filters = new JsonObject()
            .put(KName.KEY, "file-key-001")
            .put("Range", "bytes=1024-");
        final String rangeHeader = filters.getString("Range");
        final FileRange range = FileRange.of(rangeHeader);
        Assertions.assertNotNull(range);
        Assertions.assertEquals(1024L, range.getStart());
        Assertions.assertNull(range.getEnd());
    }
}
