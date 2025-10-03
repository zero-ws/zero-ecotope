package io.zerows.support;

import io.vertx.core.json.JsonArray;

import java.math.BigDecimal;

/**
 * @author lang : 2023-06-19
 */
class _Math extends _Log {
    /*
     * Math method
     * 1) mathMultiply
     * 2) mathSumDecimal
     * 3) mathSumInteger
     * 4) mathSumLong
     */
    public static Integer mathMultiply(final Integer left, final Integer right) {
        return Numeric.mathMultiply(left, right);
    }

    public static BigDecimal mathSumDecimal(final JsonArray source, final String field) {
        return Numeric.mathJSum(source, field, BigDecimal.class);
    }

    public static Integer mathSumInteger(final JsonArray source, final String field) {
        return Numeric.mathJSum(source, field, Integer.class);
    }

    public static Long mathSumLong(final JsonArray source, final String field) {
        return Numeric.mathJSum(source, field, Long.class);
    }
}
