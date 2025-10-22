package io.zerows.support.base;

import io.r2mo.base.util.R2MO;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2023/4/27
 */
class _Is extends _Io {
    protected _Is() {
    }

    // ---------------- 判断函数：空/非空

    /**
     * 检查传入的字符串是否空字符串
     * 1. 长度为0，返回 true
     * 2. 否则执行 trim，调用 isEmpty 检查
     *
     * @param input 输入字符串
     *
     * @return 是否空字符串
     */
    public static boolean isNil(final String input) {
        return TIs.isNil(input);
    }

    /**
     * isNil重载版本，检查是否空Json对象
     *
     * @param input 输入Json对象
     *
     * @return 是否空Json对象
     */
    public static boolean isNil(final JsonObject input) {
        return TIs.isNil(input);
    }

    /**
     * isNil重载版本，检查是否空Json数组
     *
     * @param input 输入Json数组
     *
     * @return 是否空Json数组
     */
    public static boolean isNil(final JsonArray input) {
        return TIs.isNil(input);
    }

    /**
     * isNil的逆函数，检查是否非空字符串
     *
     * @param input 输入字符串
     *
     * @return 是否非空字符串
     */
    public static boolean isNotNil(final String input) {
        return !isNil(input);
    }

    /**
     * isNil的逆函数，检查是否非空Json对象
     *
     * @param input 输入Json对象
     *
     * @return 是否非空Json对象
     */
    public static boolean isNotNil(final JsonObject input) {
        return !isNil(input);
    }

    /**
     * isNil的逆函数，检查是否非空Json数组
     *
     * @param input 输入Json数组
     *
     * @return 是否非空Json数组
     */
    public static boolean isNotNil(final JsonArray input) {
        return !isNil(input);
    }

    // ---------------- 判断函数：空/非空（动参版）

    /**
     * 「动参版本」
     * isNil的字符串特殊版本
     *
     * @param args 字符串数组
     *
     * @return 是否全部为空字符串
     */
    public static boolean isNil(final String... args) {
        return 0 == args.length || Arrays.stream(args).allMatch(TIs::isNil);
    }

    /**
     * 「动参版本」
     * isNil的Json对象特殊版本
     *
     * @param args Json对象数组
     *
     * @return 是否全部为空Json对象
     */
    public static boolean isNotNil(final String... args) {
        return 0 != args.length && Arrays.stream(args).noneMatch(TIs::isNil);
    }

    /**
     * 「动参版本」
     * 检查只有 notnull 参数为非空，而其他参数必须为空
     *
     * @param notnull 非空字符串
     * @param args    字符串数组
     *
     * @return 是否只有 notnull 参数为非空
     */
    public static boolean isOneOk(final String notnull, final String... args) {
        return UtBase.isNotNil(notnull) && UtBase.isNil(args);
    }

    /**
     * 「动参版本」
     * 检查传入的对象是全部为null
     * 1. 长度为0时返回 true
     * 2. 否则调用 Arrays.stream(args).allMatch(Objects::isNull)
     *
     * @param args 对象数组
     *
     * @return 是否全部为null
     */
    public static boolean isNull(final Object... args) {
        return 0 == args.length || Arrays.stream(args)
            .allMatch(Objects::isNull);
    }

    /**
     * 「动参版本」
     * 检查传入的对象是全部为非null（参数检查可用）
     * 1. 长度为0时返回 true
     * 2. 否则调用 Arrays.stream(args).allMatch(Objects::nonNull)
     *
     * @param args 对象数组
     *
     * @return 是否全部为非null
     */
    public static boolean isNotNull(final Object... args) {
        // Avoid Null Pointer
        return Objects.nonNull(args)
            && (0 == args.length || Arrays.stream(args).allMatch(Objects::nonNull));
    }

    /**
     * 「动参版本」
     * 检查传入的对象是否全部是正整数
     *
     * @param numbers 对象数组（基础类型）
     *
     * @return 是否全部是正整数
     */
    public static boolean isPositive(final int... numbers) {
        return R2MO.isPositive(numbers);
    }

    /**
     * 「动参版本」
     * 检查传入的对象是否全部是正整数
     *
     * @param numbers 对象数组
     *
     * @return 是否全部是正整数
     */
    public static boolean isPositive(final Integer... numbers) {
        return R2MO.isPositive(numbers);
    }


    // ---------------- 判断函数：特殊

    /**
     * 检查传入的字符串是否为UUID格式
     *
     * @param input 输入字符串
     *
     * @return 是否为UUID格式
     */
    public static boolean isUUID(final String input) {
        return TIs.isUUID(input);
    }

    /**
     * 对象比较函数，比较两个对象是否不等，带 null 检查的版本
     *
     * @param left  左对象
     * @param right 右对象
     *
     * @return 是否相等
     */
    public static boolean isDiff(final Object left, final Object right) {
        return !_Compare.isSame(left, right);
    }

    // ---------------- 判断函数：类型（反射）

    /**
     * 检查传入类型是否基本类型
     *
     * @param clazz 类型
     *
     * @return 是否基本类型
     */
    public static boolean isPrimary(final Class<?> clazz) {
        return TTo.isPrimary(clazz);
    }

    /**
     * 检查传入类型是否集合类型（默认不包含 [] 数组）
     *
     * @param value 对象
     *
     * @return 是否集合类型
     */
    public static boolean isCollection(final Object value) {
        return value instanceof Collection<?>;
    }

    /**
     * 检查传入类型是否数组类型
     *
     * @param value 对象
     *
     * @return 是否数组类型
     */
    public static boolean isArray(final Object value) {
        return TType.isArray(value);
    }


    /**
     * 检查传入类型是否布尔类型
     *
     * @param clazz 类型
     *
     * @return 是否布尔类型
     */
    public static boolean isBoolean(final Class<?> clazz) {
        return TType.isBoolean(clazz);
    }

    /**
     * 检查传入类型是否void类型
     *
     * @param clazz 类型
     *
     * @return 是否void类型
     */
    public static boolean isVoid(final Class<?> clazz) {
        return TType.isVoid(clazz);
    }

    /**
     * 检查传入类型是否整数类型
     *
     * @param clazz 类型
     *
     * @return 是否整数类型
     */
    public static boolean isInteger(final Class<?> clazz) {
        return TType.isInteger(clazz);
    }

    /**
     * 检查传入类型是否浮点数类型
     *
     * @param clazz 类型
     *
     * @return 是否浮点数类型
     */
    public static boolean isDecimal(final Class<?> clazz) {
        return TType.isDecimal(clazz);
    }

    /**
     * 检查传入是否数值类型
     *
     * @param clazz 类型
     *
     * @return 是否数值类型
     */
    public static boolean isNumber(final Class<?> clazz) {
        return TType.isNumber(clazz);
    }

    /**
     * 检查类型是否 JsonObject 类型
     *
     * @param clazz 类型
     *
     * @return 是否 JsonObject 类型
     */
    public static boolean isJObject(final Class<?> clazz) {
        return TType.isJObject(clazz);
    }

    /**
     * 检查一个对象是否 JsonObject 类型
     *
     * @param obj 对象
     *
     * @return 是否 JsonObject 类型
     */
    public static boolean isJObject(final Object obj) {
        return UJson.isJObject(obj);
    }

    /**
     * 检查一个字符串是否 JsonObject 类型
     *
     * @param literal 字符串
     *
     * @return 是否 JsonObject 类型
     */
    public static boolean isJObject(final String literal) {
        return UJson.isJObject(literal);
    }

    /**
     * 检查类型是否 JsonArray 类型
     *
     * @param clazz 类型
     *
     * @return 是否 JsonArray 类型
     */
    public static boolean isJArray(final Class<?> clazz) {
        return TType.isJArray(clazz);
    }

    /**
     * 检查一个对象是否 JsonArray 类型
     *
     * @param obj 对象
     *
     * @return 是否 JsonArray 类型
     */
    public static boolean isJArray(final Object obj) {
        return UJson.isJArray(obj);
    }

    /**
     * 检查一个字符串是否 JsonArray 类型
     *
     * @param literal 字符串
     *
     * @return 是否 JsonArray 类型
     */
    public static boolean isJArray(final String literal) {
        return UJson.isJArray(literal);
    }

    // ---------------- 判断函数：类型

    /**
     * 扩展参数模式，isBoolean 可检查 Object 类型，去空
     *
     * @param input 输入对象
     *
     * @return 是否合法Boolean值
     */
    public static boolean isBoolean(final Object input) {
        return R2MO.isBoolean(input);
    }

    /**
     * （默认非宽松模式）检查传入字符串是否合法Boolean值
     *
     * @param literal 字符串
     *
     * @return 是否合法Boolean值
     */
    public static boolean isBoolean(final String literal) {
        return R2MO.isBoolean(literal, false);
    }

    /**
     * 检查传入字符串是否合法Boolean值
     * 1. widely = true 时，支持 "true" / "false" / "1" / "0" / "yes" / "no" / "y" / "n"
     * 2. widely = false 时，支持 "true" / "false"
     *
     * @param literal 字符串
     * @param widely  是否宽松模式
     *
     * @return 是否合法Boolean值
     */
    public static boolean isBoolean(final String literal, final boolean widely) {
        return R2MO.isBoolean(literal, widely);
    }

    /**
     * 检查一个字符串是否正整数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否正整数
     */
    public static boolean isPositive(final String literal) {
        return R2MO.isPositive(literal);
    }

    /**
     * （传函数）函数模式专用，检查一个数值是否正整数
     *
     * @param number 数值
     *
     * @return 是否正整数
     */
    public static boolean isPositive(final int number) {
        return R2MO.isPositive(number);
    }

    /**
     * 检查一个对象是否正整数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否正整数
     */
    public static boolean isPositive(final Object input) {
        return R2MO.isPositive(input);
    }

    /**
     * 检查一个字符串是否负整数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否负整数
     */
    public static boolean isNegative(final String literal) {
        return R2MO.isNegative(literal);
    }

    /**
     * （传函数）函数模式专用，检查一个数值是否负整数
     *
     * @param number 数值
     *
     * @return 是否负整数
     */
    public static boolean isNegative(final int number) {
        return R2MO.isNegative(number);
    }

    /**
     * 检查一个对象是否负整数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否负整数
     */
    public static boolean isNegative(final Object input) {
        return R2MO.isNegative(input);
    }

    /**
     * 检查一个字符串是否整数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否整数
     */
    public static boolean isInteger(final String literal) {
        return R2MO.isInteger(literal);
    }

    /**
     * 检查一个对象是否整数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否整数
     */
    public static boolean isInteger(final Object input) {
        return R2MO.isInteger(input);
    }


    /**
     * 检查一个字符串是否实数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否数值
     */
    public static boolean isReal(final String literal) {
        return R2MO.isReal(literal);
    }

    /**
     * 检查一个对象是否实数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否数值
     */
    public static boolean isReal(final Object input) {
        return R2MO.isReal(input);
    }

    /**
     * 检查一个字符串是否浮点数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否浮点数
     */
    public static boolean isDecimal(final String literal) {
        return R2MO.isDecimal(literal);
    }

    /**
     * 检查一个对象是否浮点数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否浮点数
     */
    public static boolean isDecimal(final Object input) {
        return R2MO.isDecimal(input);
    }

    /**
     * 检查一个字符串是否正浮点数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否正浮点数
     */
    public static boolean isDecimalPositive(final String literal) {
        return R2MO.isDecimalPositive(literal);
    }

    /**
     * 检查一个对象是否正浮点数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否正浮点数
     */
    public static boolean isDecimalPositive(final Object input) {
        return R2MO.isDecimalPositive(input);
    }

    /**
     * 检查一个字符串是否负浮点数，正则表达式模式
     *
     * @param literal 字符串
     *
     * @return 是否负浮点数
     */
    public static boolean isDecimalNegative(final String literal) {
        return R2MO.isDecimalNegative(literal);
    }

    /**
     * 检查一个对象是否负浮点数，非空场景中转换成 String 检查
     *
     * @param input 对象
     *
     * @return 是否负浮点数
     */
    public static boolean isDecimalNegative(final Object input) {
        return R2MO.isDecimalNegative(input);
    }

    /**
     * 检查字符串是否是一个操作系统的合法文件名，非空正则模式
     *
     * @param filename 文件名
     *
     * @return 是否是一个操作系统的合法文件名
     */
    public static boolean isFileName(final String filename) {
        return TIs.isFileName(filename);
    }


    // --------------------------- 范围函数 ---------------------------

    /**
     * 检查 findRunning 是否位于 min 和 max 之间的闭区间
     *
     * @param value 待检查的值
     * @param min   最小值
     * @param max   最大值
     *
     * @return 是否位于 min 和 max 之间的闭区间
     */
    public static boolean isIn(final Integer value, final Integer min, final Integer max) {
        return R2MO.isIn(value, min, max);
    }

    /**
     * 检查 input 中是否包含了 fields 中的所有属性值，
     * 且所有属性值都是通过了非空合法检查，包括 String, JsonObject, JsonArray 等
     *
     * @param input  待检查的对象
     * @param fields 属性列表
     *
     * @return 是否包含了 fields 中的所有属性值
     */
    public static boolean isIn(final JsonObject input, final String... fields) {
        return UJson.isIn(input, fields);
    }

    /**
     * 检查 input 中是否包含了 fields 中的所有属性值，
     * 且所有属性值都是通过了非空合法检查，包括 String, JsonObject, JsonArray 等
     *
     * @param input  待检查的对象
     * @param fields 属性列表
     *
     * @return 是否包含了 fields 中的所有属性值
     */
    public static boolean isIn(final JsonObject input, final Set<String> fields) {
        return UJson.isIn(input, fields.toArray(new String[]{}));
    }

    /**
     * 检查 input 中是否包含了 fields 中的所有属性值，
     * 且所有属性值都是通过了非空合法检查，包括 String, JsonObject, JsonArray 等
     *
     * @param input  待检查的对象
     * @param fields 属性列表
     *
     * @return 是否包含了 fields 中的所有属性值
     */
    public static boolean isIn(final JsonObject input, final List<String> fields) {
        return UJson.isIn(input, fields.toArray(new String[]{}));
    }
}
