package io.zerows.specification.emf;

import io.zerows.specification.modeling.element.HType;

import java.util.List;

/**
 * @author lang : 2023-05-13
 */
public interface EClass<T> extends HType {
    /**
     * 直接对应 Class 中映射的：instanceClassName 属性
     *
     * @return Java 类型
     */
    Class<?> instanceClassName();

    /**
     * 当前类型的父类型定义
     *
     * @return 父类型定义
     */
    List<EClass<?>> eSuperTypes();
}
