package io.zerows.support.base;

import io.r2mo.SourceReflect;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author lang : 2023/4/27
 */
final class TType {
    private TType() {
    }

    static boolean isBoolean(final Class<?> clazz) {
        return Boolean.class.isAssignableFrom(clazz)
            || boolean.class.isAssignableFrom(clazz);
    }

    static boolean isVoid(final Class<?> clazz) {
        return void.class.isAssignableFrom(clazz)
            || Void.class.isAssignableFrom(clazz);
    }

    static boolean isInteger(final Class<?> clazz) {
        return Integer.class.isAssignableFrom(clazz)
            || int.class.isAssignableFrom(clazz)
            || Long.class.isAssignableFrom(clazz)
            || long.class.isAssignableFrom(clazz)
            || Short.class.isAssignableFrom(clazz)
            || short.class.isAssignableFrom(clazz)
            // 追加 BigInteger
            || BigInteger.class.isAssignableFrom(clazz);
    }

    static boolean isDecimal(final Class<?> clazz) {
        return Double.class.isAssignableFrom(clazz)
            || double.class.isAssignableFrom(clazz)
            || Float.class.isAssignableFrom(clazz)
            || float.class.isAssignableFrom(clazz)
            // 追加 BigDecimal
            || BigDecimal.class.isAssignableFrom(clazz);
    }

    static boolean isNumber(final Class<?> clazz) {
        return isDecimal(clazz)
            || isInteger(clazz)
            || Number.class.isAssignableFrom(clazz);
    }

    static boolean isJObject(final Class<?> clazz) {
        return JsonObject.class.isAssignableFrom(clazz)
            || LinkedHashMap.class.isAssignableFrom(clazz);
    }

    static boolean isClass(final Object clazz) {
        if (Objects.isNull(clazz)) {
            return false;
        }
        final Class<?> checkCls = SourceReflect.clazz(clazz.toString());
        return Objects.nonNull(checkCls);
    }

    static boolean isJArray(final Class<?> clazz) {
        return JsonArray.class.isAssignableFrom(clazz);
    }

    static boolean isArray(final Object value) {
        if (null == value) {
            return false;
        }
        return value.getClass().isArray();
    }
}
