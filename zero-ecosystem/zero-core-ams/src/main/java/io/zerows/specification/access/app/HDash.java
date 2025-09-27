package io.zerows.specification.access.app;

import io.zerows.specification.atomic.HContract;
import io.zerows.specification.development.ncloud.HNovae;

/**
 * 「驾驶舱/看板」Dash
 * <hr/>
 * 当前看板 / 驾驶舱的基本信息，包含了所属的核心引用
 * <pre><code>
 *     1. 当前所属的 {@link HNovae.HNebula} 监控端引用
 *     2. 当前所属的 {@link HBackend} 管理端真实引用
 * </code></pre>
 *
 * @author lang : 2023-05-21
 */
public interface HDash extends HContract {
    /**
     * 所属监控端引用 {@link HNovae.HNebula}
     *
     * @return {@link HNovae.HNebula}
     */
    HNovae.HNebula monitor();

    /**
     * 所属管理端引用 {@link HBackend}
     *
     * @return {@link HBackend}
     */
    HBackend backend();
}
