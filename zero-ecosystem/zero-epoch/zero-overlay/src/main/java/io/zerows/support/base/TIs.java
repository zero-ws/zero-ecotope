package io.zerows.support.base;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lang : 2023/4/27
 */
class TIs {
    static boolean isNil(final String str) {
        return Objects.isNull(str) || str.trim().isEmpty();
    }

    static boolean isNil(final JsonObject inputJ) {
        return Objects.isNull(inputJ) || inputJ.isEmpty();
    }

    static boolean isNil(final JsonArray inputA) {
        return Objects.isNull(inputA) || inputA.isEmpty();
    }

    static boolean isUUID(final String literal) {
        UUID converted;
        try {
            converted = UUID.fromString(literal);
        } catch (final IllegalArgumentException ex) {
            converted = null;
        }
        return Objects.nonNull(converted);
    }

    static boolean isSame(final Object left, final Object right) {
        if (Objects.isNull(left) && Objects.isNull(right)) {
            return true;
        } else {
            return Objects.nonNull(left) && Objects.nonNull(right) && left.equals(right);
        }
    }
    
    static boolean isMatch(final String value, final String regex) {
        if (isNil(value) || isNil(regex)) {
            return false;
        }
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


    static boolean isFileName(final String literal) {
        return Objects.nonNull(literal)
            && isMatch(literal, VString.REGEX.FILENAME);
    }
}
