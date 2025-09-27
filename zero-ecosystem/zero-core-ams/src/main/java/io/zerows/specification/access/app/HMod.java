package io.zerows.specification.access.app;

import io.zerows.specification.development.compiled.HBundle;
import io.zerows.specification.modeling.norm.HNs;

/**
 * 「模块」
 * 当前模块所属应用的详细信息，包括应用的基本信息，应用的部署信息，应用的版本信息等等，主要包含
 * <pre><code>
 *     1. 所属应用
 *     2. 名空间（默认为null）
 *     3. Bundle（OSGI环境才使用）
 * </code></pre>
 * 模块接口针对 {@link HApp} 会有特殊的处理逻辑，同时包含了设置和获取两种方法
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
     *    2. app != null, {@see appId/name}
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

    default <T> T value(final String field) {
        return this.value(field, null);
    }

    <T> T value(String field, T defaultValue);
}
