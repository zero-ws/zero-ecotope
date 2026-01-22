package io.zerows.extension.module.rbac.component.acl.rapid;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.module.rbac.common.ScConstant;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Set;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Slf4j
class SiftRow {

    static JsonObject onAcl(final JsonObject rows, final Set<String> visible) {
        final JsonObject result = new JsonObject();
        final JsonArray fields = Ut.valueJArray(rows.getJsonArray("condition"));
        if (Ut.isNotNil(fields)) {
            Ut.itJArray(fields, String.class, (field, index) -> {
                final JsonArray visibleFields = Ut.toJArray(visible);
                result.put(field, visibleFields);
            });
            return result;
        } else {
            return null;
        }
    }

    /*
     * rows -> JsonArray
     */
    static JsonArray onRows(final JsonArray input, final JsonObject rows) {
        final JsonArray result = new JsonArray();
        final JsonObject rowData = Ut.valueJObject(rows);
        if (rowData.isEmpty()) {
            /*
             * Do not do any row filters.
             */
            result.addAll(input);
        } else {
            log.info("{} DataRegion 行过滤 = {}", ScConstant.K_PREFIX, rowData.encode());
            input.stream().filter(Objects::nonNull)
                .map(item -> (JsonObject) item)
                .filter(item -> isMatch(item, rowData))
                .forEach(result::add);
        }
        return result;
    }

    private static boolean isMatch(final JsonObject item, final JsonObject rows) {
        /*
         * Multi fields here
         */
        return rows.fieldNames().stream().anyMatch(field -> {
            final Object inputValue = item.getValue(field);
            final JsonArray rowsArray = rows.getJsonArray(field);
            return rowsArray.contains(inputValue);
        });
    }
}
