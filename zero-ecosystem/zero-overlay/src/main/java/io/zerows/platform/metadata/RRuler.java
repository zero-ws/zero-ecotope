package io.zerows.platform.metadata;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.support.base.UtBase;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author lang : 2023-06-11
 */
public final class RRuler {
    private RRuler() {
    }

    public static JsonArray required(final JsonArray source, final RRule rule) {
        /* required fields */
        return rulerAnd(source, rule.getRequired(), value -> UtBase.isNotNil(value.toString()));
    }

    public static JsonArray duplicated(final JsonArray source, final RRule rule) {
        /* unique field */
        final Set<JsonObject> added = new HashSet<>();
        return ruler(source, rule.getUnique(), json -> {
            final JsonObject uniqueJson = UtBase.elementSubset(json, rule.getUnique());
            if (added.contains(uniqueJson)) {
                return false;
            } else {
                added.add(uniqueJson);
                return true;
            }
        });
    }

    private static JsonArray rulerAnd(final JsonArray source, final Set<String> fieldSet, final Predicate<Object> fnFilter) {
        return ruler(source, fieldSet, json -> fieldSet.stream().allMatch(field -> {
            final Object value = json.getValue(field);
            if (Objects.nonNull(value)) {
                return fnFilter.test(value);
            } else {
                return false;
            }
        }));
    }

    private static JsonArray rulerOr(final JsonArray source, final Set<String> fieldSet, final Predicate<Object> fnFilter) {
        return ruler(source, fieldSet, json -> fieldSet.stream().anyMatch(field -> {
            final Object value = json.getValue(field);
            if (Objects.nonNull(value)) {
                return fnFilter.test(value);
            } else {
                return false;
            }
        }));
    }

    private static JsonArray ruler(final JsonArray source, final Set<String> fieldSet, final Predicate<JsonObject> fnFilter) {
        if (fieldSet.isEmpty()) {
            return source;
        } else {
            /* Code Logical */
            final JsonArray processed = new JsonArray();
            UtBase.itJArray(source).filter(fnFilter).forEach(processed::add);
            return processed;
        }
    }
}
