package io.zerows.epoch.program;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;

import java.math.BigDecimal;

/**
 * Number checking
 */
final class Numeric {
    private static final OLog LOGGER = _Log.Log.ux(Numeric.class);

    private Numeric() {
    }

    static Integer mathMultiply(final Integer left, final Integer right) {
        return Math.multiplyExact(left, right);
    }

    @SuppressWarnings("unchecked")
    static <T> T mathJSum(final JsonArray source, final String field, final Class<T> clazz) {
        Object returnValue = null;
        if (Double.class == clazz || BigDecimal.class == clazz) {
            final double result = source.stream().mapToDouble(item -> JsonObject.mapFrom(item).getDouble(field)).sum();
            returnValue = BigDecimal.class == clazz ? new BigDecimal(result) : result;
        } else if (Long.class == clazz) {
            returnValue = source.stream().mapToLong(item -> JsonObject.mapFrom(item).getLong(field)).sum();
        } else if (Integer.class == clazz) {
            returnValue = source.stream().mapToInt(item -> JsonObject.mapFrom(item).getInteger(field)).sum();
        } else {
            LOGGER.error(Info.MATH_NOT_MATCH, clazz);
        }
        return null == returnValue ? null : (T) returnValue;
    }
}
