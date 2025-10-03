package io.zerows.support;

import io.zerows.constant.VString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2023/4/27
 */
class TString {
    private TString() {
    }

    private static String repeat(final Integer times, final char fill) {
        return String.valueOf(fill).repeat(Math.max(0, times));
    }

    static String fromAdjust(final String seed, final Integer width, final char fill) {
        final StringBuilder builder = new StringBuilder();
        final int seedLen = seed.length();
        int fillLen = width - seedLen;
        if (0 > fillLen) {
            fillLen = 0;
        }
        builder.append(repeat(fillLen, fill));
        builder.append(seed);
        return builder.toString();
    }


    static List<String> split(final String input, final String separator) {
        if (Objects.isNull(input) || Objects.isNull(separator)) {
            return new ArrayList<>();
        }
        final String[] array = input.split(separator);
        final List<String> result = new ArrayList<>();
        Arrays.stream(array)
            .filter(Objects::nonNull)
            .map(item -> item.trim().intern())
            .forEach(result::add);
        return result;
    }

    /*
     * Object[] could not be cast to String[] directly
     * It means here must contain below method to process it.
     */
    static String join(final Object[] input, final String separator) {
        final Set<String> hashSet = new HashSet<>();
        Arrays.stream(input).filter(Objects::nonNull)
            .map(Object::toString).forEach(hashSet::add);
        return join(hashSet, separator);
    }

    static String join(final Collection<String> input, final String separator) {
        final String connector = (null == separator) ? VString.COMMA : separator;
        final StringBuilder builder = new StringBuilder();
        final int size = input.size();
        int start = 0;
        for (final String item : input) {
            builder.append(item);
            start++;
            if (start < size) {
                builder.append(connector);
            }
        }
        return builder.toString();
    }
}
