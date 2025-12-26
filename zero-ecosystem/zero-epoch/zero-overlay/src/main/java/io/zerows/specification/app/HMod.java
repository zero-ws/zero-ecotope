package io.zerows.specification.app;

import io.zerows.specification.development.compiled.HBundle;
import io.zerows.specification.modeling.norm.HNs;

/**
 * 「模块」
 * 当前模块所属应用的详细信息，包括应用的基本信息，应用的部署信息，应用的版本信息等等，主要包含
 * <pre><code>
 *     1. 所属应用
 *     2. 名空间（默认为null）
 *     3. {@link HBundle}
 * </code></pre>
 * 模块接口针对 {@link HApp} 会有特殊的处理逻辑，主要包含如下功能
 * <pre>
 *     1. 绑定范围信息
 *        - 所属名空间 {@link HNs.HMeta}，不用担心此处的名空间本身的问题，名空间使用软链接计算得到
 *        - 所属Bundle：{@link HBundle}
 *          - 在 OSGI 环境中会对应发布模块的 Bundle 信息
 *          - 非 OSGI 环境中，根据提供的不同实现会有不同的处理，如本地环境和云环境（分布式）
 *     2. 创建应用关联：{@link HApp} 应用信息的设置获取
 *     3. 模块的基本信息
 *        - 模块唯一标识 id()
 *        - 模块名称 name()：配置数据提取名称信息，通常以 m 前缀开头
 *     4. 模块的属性信息
 *        - value(field)：根据字段提取模块的属性值
 *        - value(field, defaultValue)：根据字段提取模块的属性值，若无则使用默认值
 * </pre>
 *
 * @author lang : 2023-05-21
 */
public interface HMod {

    /**
     * 所属命名空间
     *
     * @return {@link HNs.HMeta}
     */
    default HNs.HMeta namespace() {
        return null;
    }

    /**
     * 当前模块隶属于哪个 Bundle 发布
     *
     * @return {@link HBundle}
     */
    default HBundle bundle() {
        return null;
    }

    // -------------- 应用相关引用
    HApp app();

    HMod app(HApp appRef);

    // -------------- 基础模块信息

    /**
     * 当前模块的唯一标识，此标识的计算规则
     * <pre><code>
     *    1. app = null,  {@see * /name}
     *    2. app != null, {@see id/name}
     * </code></pre>
     *
     * @return 返回标识值
     */
    String id();

    /**
     * 当前存储的模块对应的名称，一般以 m 前缀，如
     * <pre><code>
     *     mHotel
     *     mSetting
     *     mSecurity
     * </code></pre>
     *
     * @return 模块名称
     */
    String name();

    // -------------- 模块属性信息
    default <T> T value(final String field) {
        return this.value(field, null);
    }

    <T> T value(String field, T defaultValue);

}
