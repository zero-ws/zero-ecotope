package io.zerows.support;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VEnv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2023/4/27
 */
final class TTo {
    private TTo() {
    }

    static Class<?> toPrimary(final Class<?> source) {
        return VEnv.SPEC.TYPES.getOrDefault(source, source);
    }

    static <T extends Enum<T>> T toEnum(final String literal, final Class<T> clazz, final T defaultEnum) {
        if (TIs.isNil(literal) || Objects.isNull(clazz)) {
            return defaultEnum;
        }
        return Enum.valueOf(clazz, literal);
    }

    static List<String> toList(final JsonArray keys) {
        final JsonArray keysData = UtBase.valueJArray(keys);
        final List<String> keyList = new ArrayList<>();
        UIterator.itJString(keysData).forEach(keyList::add);
        return keyList;
    }

    static Collection<?> toCollection(final Object value) {
        // Collection
        if (value instanceof Collection) {
            return ((Collection<?>) value);
        }
        // JsonArray
        if (UtBase.isJArray(value)) {
            return ((JsonArray) value).getList();
        }
        // Object[]
        if (UtBase.isArray(value)) {
            // Array
            final Object[] values = (Object[]) value;
            return Arrays.asList(values);
        }
        return null;
    }


    static String toString(final Object reference) {
        if (Objects.isNull(reference)) {
            return "null";
        }
        final String literal;
        if (UtBase.isJObject(reference)) {
            // Fix issue for serialization
            literal = ((JsonObject) reference).encode();
        } else if (UtBase.isJArray(reference)) {
            // Fix issue for serialization
            literal = ((JsonArray) reference).encode();
        } else {
            literal = reference.toString();
        }
        return literal;
    }
}
