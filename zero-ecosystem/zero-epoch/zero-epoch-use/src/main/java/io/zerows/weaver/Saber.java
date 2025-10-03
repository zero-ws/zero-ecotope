package io.zerows.weaver;

/**
 * For field serialization
 */
public interface Saber {
    /**
     * String to object
     *
     * @param type    数据类型
     * @param literal 字面量
     *
     * @return 转换后的对象
     */
    Object from(Class<?> type, String literal);

    /**
     * Tool to object
     *
     * @param input 输入数据
     * @param <T>   泛型类型
     *
     * @return 转换后的对象
     */
    <T> Object from(T input);
}
