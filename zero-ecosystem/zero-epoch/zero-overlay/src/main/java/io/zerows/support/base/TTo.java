package io.zerows.support.base;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2023/4/27
 */
final class TTo {
    private static final ConcurrentMap<Class<?>, Class<?>> TYPES = new ConcurrentHashMap<Class<?>, Class<?>>() {
        {
            this.put(Integer.class, int.class);
            this.put(Long.class, long.class);
            this.put(Short.class, short.class);
            this.put(Boolean.class, boolean.class);
            this.put(Character.class, char.class);
            this.put(Double.class, double.class);
            this.put(Float.class, float.class);
            this.put(Byte.class, byte.class);
        }
    };

    private TTo() {
    }

    static boolean isPrimary(final Class<?> clazz) {
        return TYPES.containsValue(clazz);
    }

    static Class<?> toPrimary(final Class<?> source) {
        return TYPES.getOrDefault(source, source);
    }

    static <T extends Enum<T>> T toEnum(final String literal, final Class<T> clazz, final T defaultEnum) {
        if (TIs.isNil(literal) || Objects.isNull(clazz)) {
            return defaultEnum;
        }
        try {
            return Enum.valueOf(clazz, literal);
        } catch (final IllegalArgumentException ex) {
            // java.lang.IllegalArgumentException: No enum constant
            return defaultEnum;
        }
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
