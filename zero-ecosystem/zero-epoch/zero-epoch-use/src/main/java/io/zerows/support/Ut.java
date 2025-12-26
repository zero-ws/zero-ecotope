package io.zerows.support;

import io.r2mo.SourceReflect;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.lang.reflect.Field;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public final class Ut extends _Visit {
    private Ut() {
    }

    /*
     *
     * 1) inverseCount
     * 2) inverseSet
     */

    public static boolean uriMatch(final String uri, final String pattern) {
        final Pattern compiler = Uri.createRegex(pattern);
        return compiler.matcher(uri).matches();
    }

    // This is usage in case1 for integration, that's why keep here
    //    public static String encryptRSAPIo(final String input, final String keyPath) {
    //        final String keyContent = Ut.ioString(keyPath);
    //        return ED.rsa(true).encrypt(input, keyContent);
    //    }
    /*
     * Comparing method of two
     * 1) compareTo: Generate comparing method to return int
     */
    public static int compareTo(final int left, final int right) {
        return Compare.compareTo(left, right);
    }

    public static int compareTo(final String left, final String right) {
        return Compare.compareTo(left, right);
    }

    public static <T> int compareTo(final T left, final T right, final BiFunction<T, T, Integer> fnCompare) {
        return Compare.compareTo(left, right, fnCompare);
    }

    public static <T> T plugin(final JsonObject options, final String pluginKey, final Class<T> interfaceCls) {
        return Instance.plugin(options, pluginKey, interfaceCls);
    }

    public static Field[] fields(final Class<?> clazz) {
        return SourceReflect.fields(clazz);
    }

    public static <T, V> void contract(final T instance, final Class<?> fieldType, final V value) {
        Instance.contract(Instance.class, instance, fieldType, value);
    }

    public static <T, V> Future<Boolean> contractAsync(final T instance, final Class<?> fieldType, final V value) {
        contract(instance, fieldType, value);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
