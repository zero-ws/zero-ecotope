package io.zerows.support.base;

import io.vertx.core.json.JsonArray;
import io.zerows.specification.modeling.HRecord;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author lang : 2023-05-08
 */
class MModeler {
    private MModeler() {
    }

    static JsonArray toJArray(final HRecord[] records) {
        final JsonArray result = new JsonArray();
        if (Objects.nonNull(records)) {
            Arrays.stream(records).map(HRecord::toJson)
                .forEach(result::add);
        }
        return result;
    }
}
