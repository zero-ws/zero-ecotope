package io.zerows.support.base;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.constant.VString;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023/4/27
 */
class _To extends _Serialize {
    protected _To() {
    }

    /**
     * 根据传入类型将该类型转换成基本类型
     *
     * @param source 源类型
     * @return 基本类型
     */
    public static Class<?> toPrimary(final Class<?> source) {
        return TTo.toPrimary(source);
    }

    /**
     * 根据传入枚举类型做字符串级别的转换
     *
     * @param clazz   枚举元类型
     * @param literal 字符串
     * @param <T>     枚举类型
     * @return 枚举
     */
    public static <T extends Enum<T>> T toEnum(final String literal, final Class<T> clazz) {
        return TTo.toEnum(literal, clazz, null);
    }

    /**
     * 根据传入枚举类型做字符串级别的转换（带默认值）
     *
     * @param clazz        枚举元类型
     * @param literal      字符串
     * @param defaultValue 默认值
     * @param <T>          枚举类型
     * @return 枚举
     */
    public static <T extends Enum<T>> T toEnum(final String literal, final Class<T> clazz, final T defaultValue) {
        return TTo.toEnum(literal, clazz, defaultValue);
    }

    /**
     * 根据传入枚举类型做字符串级别的转换（函数模式）
     *
     * @param supplier 字符串提供者
     * @param clazz    枚举元类型
     * @param <T>      枚举类型
     * @return 枚举
     */
    public static <T extends Enum<T>> T toEnum(final Supplier<String> supplier, final Class<T> clazz) {
        return TTo.toEnum(supplier.get(), clazz, null);
    }

    /**
     * 根据传入枚举类型做字符串级别的转换（函数模式带默认值）
     *
     * @param supplier     字符串提供者
     * @param clazz        枚举元类型
     * @param defaultValue 默认值
     * @param <T>          枚举类型
     * @return 枚举
     */
    public static <T extends Enum<T>> T toEnum(final Supplier<String> supplier, final Class<T> clazz, final T defaultValue) {
        return TTo.toEnum(supplier.get(), clazz, defaultValue);
    }

    /**
     * 读取所有文件信息转换成压缩文件流
     *
     * @param fileSet 文件集合
     * @return 压缩文件流
     */
    public static Buffer toZip(final Set<String> fileSet) {
        return IoZip.ioZip(fileSet);
    }

    /**
     * 将传入对象转换成字节数组
     *
     * @param message 对象
     * @param <T>     对象类型
     * @return 字节数组
     */
    public static <T> byte[] toBytes(final T message) {
        return IoStream.to(message);
    }

    /**
     * 将 JsonArray 转换 Set<String>
     *
     * @param arrayA JsonArray
     * @return Set<String>
     */
    public static Set<String> toSet(final JsonArray arrayA) {
        return new HashSet<>(TTo.toList(arrayA));
    }

    /**
     * 将 JsonArray 转换 List<String>
     *
     * @param arrayA JsonArray
     * @return List<String>
     */
    public static List<String> toList(final JsonArray arrayA) {
        return TTo.toList(arrayA);
    }

    /**
     * 将 String 转换成 List<String>，默认逗号分割
     *
     * @param literal 字符串字面量
     * @return List<String>
     */
    public static List<String> toList(final String literal) {
        return TString.split(literal, VString.COMMA);
    }

    /**
     * 将 String 转换成 List<String>，默认逗号分割
     *
     * @param literal   字符串字面量
     * @param separator 分隔符
     * @return List<String>
     */
    public static List<String> toList(final String literal, final String separator) {
        return TString.split(literal, separator);
    }

    /**
     * 将 String 转换成 Set<String>，默认逗号分割
     *
     * @param literal 字符串字面量
     * @return Set<String>
     */
    public static Set<String> toSet(final String literal) {
        return new HashSet<>(TString.split(literal, VString.COMMA));
    }

    /**
     * 将 String 转换成 Set<String>，默认逗号分割
     *
     * @param literal   字符串字面量
     * @param separator 分隔符
     * @return Set<String>
     */
    public static Set<String> toSet(final String literal, final String separator) {
        return new HashSet<>(TString.split(literal, separator));
    }

    /**
     * 将字符串字面量转换成 JsonArray
     *
     * @param literal 字符串字面量
     * @return JsonArray
     */
    public static JsonArray toJArray(final String literal) {
        return UJson.toJArray(literal);
    }

    /**
     * （非空检查）集合转换成 JsonArray
     *
     * @param set 集合
     * @param <T> 集合类型
     * @return JsonArray
     */
    public static <T> JsonArray toJArray(final Set<T> set) {
        return UJson.toJArray(set);
    }

    /**
     * （非空检查）集合转换成 JsonArray
     *
     * @param list 集合
     * @param <T>  集合类型
     * @return JsonArray
     */
    public static <T> JsonArray toJArray(final List<T> list) {
        return UJson.toJArray(list);
    }

    /**
     * 增强型转换，可注入转换函数执行附加操作构造新的 JsonArray
     *
     * @param literal 字符串字面量
     * @param itemFn  转换函数
     * @return JsonArray
     */
    public static JsonArray toJArray(final String literal, final Function<JsonObject, JsonObject> itemFn) {
        return UJson.toJArray(literal, itemFn);
    }

    /**
     * 增强型转换，可注入转换函数执行附加操作构造新的 JsonArray
     *
     * @param array    JsonArray
     * @param executor 转换函数
     * @return JsonArray
     */
    public static JsonArray toJArray(final JsonArray array, final Function<JsonObject, JsonObject> executor) {
        return UJson.toJArray(array, executor);
    }

    /**
     * 智能转换，对对象进行类型判断，转换成 JsonArray
     *
     * @param value 对象
     * @return JsonArray
     */
    public static JsonArray toJArray(final Object value) {
        return UJson.toJArray(value);
    }


    /**
     * （带非空检查）Map转换成 JsonObject
     *
     * @param map Map
     * @return JsonObject
     */
    public static JsonObject toJObject(final Map<String, Object> map) {
        return UJson.toJObject(map);
    }

    /**
     * 增强型转换，可注入转换函数执行附加操作构造新的 JsonObject
     *
     * @param literal 字符串字面量
     * @param itemFn  转换函数
     * @return JsonObject
     */
    public static JsonObject toJObject(final String literal, final Function<JsonObject, JsonObject> itemFn) {
        return UJson.toJObject(literal, itemFn);
    }

    /**
     * 字符串转换成 JsonObject，如果有错默认返回空对象
     *
     * @param literal 字符串
     * @return JsonObject
     */
    public static JsonObject toJObject(final String literal) {
        return UJson.toJObject(literal);
    }

    /**
     * 智能转换，对对象进行类型判断
     *
     * @param value 对象
     * @return JsonObject
     */
    public static JsonObject toJObject(final Object value) {
        return UJson.toJObject(value);
    }

    /**
     * 智能转换，将对象转换成 String 类型
     *
     * @param value 对象
     * @return String
     */
    public static String toString(final Object value) {
        return TTo.toString(value);
    }

    /**
     * 只能转换，将对象转换成 Collection 类型
     *
     * @param value 对象
     * @return Collection
     */
    public static Collection<?> toCollection(final Object value) {
        return TTo.toCollection(value);
    }
}
