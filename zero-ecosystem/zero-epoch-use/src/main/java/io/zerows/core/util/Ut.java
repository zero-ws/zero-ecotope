package io.zerows.core.util;

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

    /*
     * Reflection method to create instance
     * 1) instance
     * 2) singleton ( Global singleton object reference )
     *
     * Reflection method to be as action
     * 1) invoke / invokeAsync
     * 2) clazz
     * 3) isImplement
     * 4) proxy
     * 5) withNoArgConstructor
     * 6) childUnique
     * 7) field / fields
     * 8) contract / contractAsync ( @Contract )
     * 9) plugin
     * 10) service ( By Service Loader )
     */

    /**
     * 将 service 重命名，保证和原始的 service 不冲突
     * serviceChannel 会先检查 /META-INF/services/aeon/ 下的定义，如果没有则使用默认的
     *
     * @param interfaceCls
     * @param <T>
     *
     * @return
     */
    public static <T> T serviceChannel(final Class<T> interfaceCls) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Service.load(interfaceCls, classLoader);
    }

    public static <T> T plugin(final JsonObject options, final String pluginKey, final Class<T> interfaceCls) {
        return Instance.plugin(options, pluginKey, interfaceCls);
    }

    public static Class<?> child(final Class<?> clazz) {
        return Instance.child(clazz);
    }

    public static Field[] fields(final Class<?> clazz) {
        return Instance.fields(clazz);
    }

    public static <T, V> void contract(final T instance, final Class<?> fieldType, final V value) {
        Instance.contract(Instance.class, instance, fieldType, value);
    }

    public static <T, V> Future<Boolean> contractAsync(final T instance, final Class<?> fieldType, final V value) {
        contract(instance, fieldType, value);
        return Future.succeededFuture(Boolean.TRUE);
    }
}
