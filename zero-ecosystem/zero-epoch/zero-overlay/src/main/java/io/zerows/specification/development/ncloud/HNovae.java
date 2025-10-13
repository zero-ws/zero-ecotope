package io.zerows.specification.development.ncloud;

import io.zerows.platform.enums.EmCloud;
import io.zerows.platform.metadata.KRepo;
import io.zerows.specification.atomic.HCommand;
import io.zerows.specification.atomic.HContract;

import java.util.concurrent.ConcurrentMap;

/**
 * 「持续在线」
 * 管理者：新星（复），管理端专用，用于描述和管理端容器相关的相关数据和上层引用的区域强引用关系
 * <pre><code>
 *     1. 所属关联直接使用 {@link HCube}
 *     2. 管理端的所有部署集合和管理端应用（反向引用）
 * </code></pre>
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HNovae extends HContract.Port, HCommand.Async<HAeon, Boolean> {
    /**
     * 直接对应到 {@link HCube} 引用，所属应用区域
     *
     * @return {@link HCube}
     */
    default HCube cube() {
        return null;
    }

    /**
     * 「持续在线」
     * 观察者：星云，监控端专用，用于描述和监控容器相关的监控端相关数据和上层应用区域的强引用关系
     *
     * <pre><code>
     *     1. 所属关联直接使用 {@link HCube}
     *     2. 监控端所有驾驶舱集合（反向引用）
     * </code></pre>
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HNebula extends Port {
        /**
         * 直接对应到 {@link HCube} 引用，所属应用区域
         *
         * @return {@link HCube}
         */
        default HCube cube() {
            return null;
        }
    }

    /**
     * 「持续在线」
     * 执行者：新星，运行端专用，用于描述和核心容器相关的运行端相关数据和上层的应用区域是强引用关系
     * <pre><code>
     *     1. 所属关联直接使用 {@link HCube}
     *     2. 运行端的所有容器运行时集合（反向引用）
     * </code></pre>
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HOne extends Port, Async<ConcurrentMap<EmCloud.Runtime, KRepo>, Boolean> {
        /**
         * 直接对应到 {@link HCube} 引用，所属应用区域
         *
         * @return {@link HCube}
         */
        default HCube cube() {
            return null;
        }
    }
}
