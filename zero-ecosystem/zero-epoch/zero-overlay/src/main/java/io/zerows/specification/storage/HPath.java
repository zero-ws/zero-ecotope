package io.zerows.specification.storage;

import io.r2mo.io.common.HFS;
import io.zerows.platform.annotations.meta.One2One;
import io.zerows.platform.enums.OS;
import io.zerows.specification.modeling.norm.HUri;

/**
 * 「文件路径」
 * <hr/>
 * 抽象文件路径，用于标识文件的唯一路径，直接和网络文件路径对接实现命名服务模式
 * <pre><code>
 *     1. 实体建模分两种
 *        - 直接对应 HPath 做文件建模
 *        - 对接 HDB 做数据库级建模
 * </code></pre>
 *
 * @author lang : 2023-05-21
 */
public interface HPath {
    /**
     * 路径可直接关联到存储对象，实现 1:1，支持的类型如：
     * <pre><code>
     *     1. {@link HFS}
     *     2. {@link HChunk}
     * </code></pre>
     *
     * @return {@link HStoreLegacy}
     */
    @One2One
    HStoreLegacy reference();

    /**
     * 文件路径详细描述（绝对路径）
     *
     * @return {@link String}
     */
    String path();

    /**
     * 操作系统类型
     *
     * @return {@link OS}
     */
    OS os();

    /**
     * 关联网络标识
     *
     * @return {@link HUri}
     */
    @One2One
    HUri uri();
}
