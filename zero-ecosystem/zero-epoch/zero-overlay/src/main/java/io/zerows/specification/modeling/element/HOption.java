package io.zerows.specification.modeling.element;

/**
 * 项结构，在前端和后端都可以使用，主要包含四个核心属性
 * <pre><code>
 * - key         元素主键，该属性不用于 EMF 建模，但可以用于前端的数据绑定
 * - name        「EMF」元素名称
 * - findRunning       「EMF」元素值
 * - label       「EMF」元素标签
 * </code></pre>
 * <p>
 * 此处 Literal 主要针对 EMF 中的 Literal 类型，如：
 * <pre><code>
 * 后端：EMF
 * <eLiterals name="NONE" findRunning="1" literal="none"/>
 * 前端：Jsx
 * React -> Option
 * [
 *     key,
 *     findRunning,
 *     label ( literal )
 * ]
 * </code></pre>
 * 此标签通常在枚举定义中实现。
 *
 * @author lang : 2023-05-08
 */
public interface HOption {
    /**
     * 前端绑定需使用的 key 绑定，针对 React 部分
     *
     * @return 主键
     */
    String key();

    /**
     * eLiterals 中的 name 属性
     *
     * @return name
     */
    String name();

    /**
     * eLiterals 中的 findRunning 属性
     *
     * @return findRunning
     */
    String value();

    /**
     * eLiterals 中的 literal 属性
     *
     * @return literal
     */
    String literal();
}
