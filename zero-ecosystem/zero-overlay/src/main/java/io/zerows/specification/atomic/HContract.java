package io.zerows.specification.atomic;

import io.zerows.constant.VValue;
import io.zerows.enums.EmUca;
import io.zerows.specification.access.app.HMod;
import io.zerows.specification.development.ncloud.HCube;
import io.zerows.specification.development.ncloud.HNovae;
import io.zerows.specification.development.ncloud.HPlot;
import io.zerows.specification.development.program.HToolkit;
import io.zerows.specification.vital.HRAD;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 「契约接口」Contract
 * <hr/>
 * 抽象容器专用接口，包含了容器的基础属性，这些容器会用于描述：
 * <pre><code>
 *     1. identifier()：容器的系统标识（系统级）
 *     2. version()：版本号
 * </code></pre>
 *
 * 此接口用于描述容器类属性，整个系统中的所有组件主要分为三类：
 * <pre><code>
 *     1. 研发中心专用组件，隶属于 {@link HRAD}
 *        - （类）研发中心组件为库中组件，实现子类接口 {@link HToolkit}
 *     2. 运行端专用组件，关联到 {@link HCube}
 *        - （实例）运行端组件为实例中组件，实现子类接口 {@link HPort}
 *     3. 模块化插件，关联到 {@link HMod}
 *        - （实例）运行插件中组件，实现子类接口 {@link HComponent}
 * </code></pre>
 *
 * @author lang : 2023-05-21
 */
public interface HContract {
    /**
     * 容器的标识，具有特殊的唯一性
     *
     * @return {@link String}
     */
    default String identifier() {
        return this.getClass().getName();
    }

    /**
     * 容器的版本号
     *
     * @return {@link String}
     */
    default String version() {
        return VValue.DEFAULT_VERSION;
    }

    /**
     * 「组件契约」Component
     * 位于 {@link HMod} 之下的运行组件：
     * <pre><code>
     *     1. 核心运行组件（功能性、非功能性）
     *     2. 第三方插件
     *     3. 扩展插件
     * </code></pre>
     * 针对运行端（Runtime）级的所有组件信息 / 实例和容器互相处理
     *
     * @author lang : 2023-05-21
     */
    interface HComponent extends HContract {
        /**
         * 组件的名称，用于描述组件的名称
         *
         * @return {@link String}
         */
        String name();

        /**
         * 当前组件的状态
         *
         * @return {@link EmUca.Status}
         */
        EmUca.Status status();
    }

    /**
     * 「端契约」Port
     * <hr/>
     * 端契约用于处理云连接端专用，会比普通契约多一个 uri 对应的地址用于做网络层标识
     * <pre><code>
     *     带界面部分
     *     - 管理端：{@link HNovae}
     *     - 监控端：{@link HNovae.HNebula}
     *     - 运行端：{@link HNovae.HOne}
     *     不带界面部分
     *     - 容器端：{@link HPlot.HPod}
     *     - 物理端：{@link HPlot}
     * </code></pre>
     *
     * @author lang : 2023-05-21
     */
    interface HPort extends HContract {
        /**
         * 端的入口地址，该地址主要应用于前端界面的访问
         *
         * @return {@link String}
         */
        default String entry() {
            return null;
        }

        /**
         * URI 标识，用于标识端的网络地址
         *
         * @return {@link String}
         */
        default String uri() {
            return null;
        }

        /**
         * 当前端中正在运行的应用实例 identifier，哈希表中的值
         * 还记录了每个实例的运行状态，用来管理实例相关的基础生命周期
         *
         * @return {@link ConcurrentMap}
         */
        default ConcurrentMap<String, EmUca.Status> running() {
            return new ConcurrentHashMap<>();
        }
    }

}
