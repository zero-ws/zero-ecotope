package io.zerows.support.base;

import io.r2mo.function.Actuator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author lang : 2023/4/27
 */
class _It extends _Is {
    protected _It() {
    }

    /**
     * 带非空 null 检查的 Set 遍历，直接返回 Stream
     *
     * @param set 集合
     * @param <V> V
     *
     * @return Stream
     */
    public static <V> Stream<V> itSet(final Set<V> set) {
        return UIterator.itSet(set);
    }

    /**
     * 带非空 null 检查的 List 遍历，直接返回 Stream
     *
     * @param list 集合
     * @param <V>  V
     *
     * @return Stream
     */
    public static <V> Stream<V> itList(final List<V> list) {
        return UIterator.itList(list);
    }


    /**
     * （带非空 null 检查）遍历 JsonArray，提取元素为 JsonObject 的类型，返回 Stream
     *
     * @param array JsonArray
     *
     * @return Stream
     */
    public static Stream<JsonObject> itJArray(final JsonArray array) {
        return UIterator.itJArray(array);
    }

    /**
     * （带非空 null 检查）遍历 JsonArray，提取元素为 JsonObject 的类型，返回 Stream
     *
     * @param array JsonArray
     * @param clazz 元素类型
     * @param <T>   T
     *
     * @return Stream
     */
    public static <T> Stream<T> itJArray(final JsonArray array, final Class<T> clazz) {
        return UIterator.itJArray(array, clazz);
    }

    /**
     * （带非空 null 检查）遍历 JsonArray，提取元素为 JsonObject 的类型，且满足条件的，返回 Stream
     *
     * @param array     JsonArray
     * @param predicate 过滤器
     *
     * @return Stream
     */
    public static Stream<JsonObject> itJArray(final JsonArray array, final Predicate<JsonObject> predicate) {
        return UIterator.itJArray(array, predicate);
    }

    /**
     * 「副作用模式」按固定的 clazz 类型提取 JsonArray 中符合类型的元素，执行 fnEach 函数
     *
     * @param array  JsonArray
     * @param clazz  元素类型
     * @param fnEach 每个元素执行的函数
     * @param <T>    T
     */
    public static <T> void itJArray(final JsonArray array, final Class<T> clazz, final BiConsumer<T, Integer> fnEach) {
        UIterator.itJArray(array, clazz, fnEach);
    }

    /**
     * 「副作用模式」按固定的 clazz 类型提取 JsonArray 中符合 JsonObject 再行的元素，执行 fnEach 函数
     *
     * @param array  JsonArray
     * @param fnEach 每个元素执行的函数
     */
    public static void itJArray(final JsonArray array, final BiConsumer<JsonObject, Integer> fnEach) {
        UIterator.itJArray(array, JsonObject.class, fnEach);
    }

    /**
     * （带非空 null 检查）遍历 JsonArray，提取元素为 String 的类型，返回 Stream
     *
     * @param array JsonArray
     *
     * @return Stream
     */
    public static Stream<String> itJString(final JsonArray array) {
        return UIterator.itJString(array);
    }

    /**
     * （带非空 null 检查）遍历 JsonArray，提取元素为 String 的类型，且满足条件的，返回 Stream
     *
     * @param array     JsonArray
     * @param predicate 过滤器
     *
     * @return Stream
     */
    public static Stream<String> itJString(final JsonArray array, final Predicate<String> predicate) {
        return UIterator.itJString(array, predicate);
    }

    /**
     * 统一遍历输入数据中的 JsonArray 或 JsonObject，最终调用函数 executor 转换 T 类型
     *
     * @param data     输入数据
     * @param executor 转换函数
     * @param <T>      T
     *
     * @return T
     */
    public static <T> T itJson(final T data, final Function<JsonObject, T> executor) {
        return UIterator.itJson(data, executor);
    }

    /**
     * 泛型遍历，将原始的 Object 类型遍历转换成带类型的遍历模式
     *
     * @param input 输入数据
     * @param <T>   T
     *
     * @return Stream
     */
    public static <T> Stream<Map.Entry<String, T>> itJObject(final JsonObject input) {
        return UIterator.itJObject(input, (Class<T>) null);
    }

    /**
     * 泛型遍历，将原始的 Object 类型遍历转换成带类型的遍历模式
     *
     * @param input 输入数据
     * @param clazz 类型
     * @param <T>   T
     *
     * @return Stream
     */
    public static <T> Stream<Map.Entry<String, T>> itJObject(final JsonObject input, final Class<T> clazz) {
        return UIterator.itJObject(input, clazz);
    }

    /**
     * 「副作用模式」泛型遍历，直接遍历一个 JsonObject 中的类型，并转换成带类型的结果
     *
     * @param data   JsonObject
     * @param fnEach 遍历函数
     * @param <T>    T
     */
    public static <T> void itJObject(final JsonObject data, final BiConsumer<T, String> fnEach) {
        UIterator.itJObject(data, fnEach);
    }

    /**
     * 按次数重复执行
     *
     * @param times    次数
     * @param actuator 执行函数
     */
    public static void itRepeat(final Integer times, final Actuator actuator) {
        UIterator.itRepeat(times, actuator);
    }
}
