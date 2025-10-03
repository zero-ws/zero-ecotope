package io.zerows.support;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.constant.VValue;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author lang : 2023/4/30
 */
class CFind {
    static JsonObject find(final JsonArray array, final String field, final Object value) {
        return UtBase.itJArray(array).filter(item -> {
            if (Objects.isNull(value)) {
                return Objects.isNull(item.getValue(field));
            } else {
                return value.equals(item.getValue(field));
            }
        }).findAny().orElse(null);
    }

    static JsonObject find(final JsonArray array, final JsonObject subsetQ) {
        return UtBase.itJArray(array).filter(item -> {
            final Set<String> keys = subsetQ.fieldNames();
            final JsonObject subset = UtBase.elementSubset(item, keys);
            return subset.equals(subsetQ);
        }).findAny().orElse(new JsonObject());
    }

    static <T> T find(final List<T> list, final Predicate<T> fnFilter) {
        final List<T> filtered = list.stream().filter(fnFilter).toList();
        if (filtered.isEmpty()) {
            return null;
        }
        return filtered.get(VValue.IDX);
    }
}
