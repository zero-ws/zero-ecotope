package io.zerows.specification.development.ncloud;

import io.zerows.specification.atomic.HContract;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * 「物理区域」Plot
 * 对应到底层的基础软件空间，直接和底层物理设备中对接，容器中会包含一个所属，用来
 * 标识当前 Plot 的边界区域，物理区域和应用区域也是多对一的处理
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HPlot extends HContract.Port {
    /**
     * 直接对应到 {@link HCube} 中的 identifier 信息，执行软关联
     *
     * @return {@link String}
     */
    String reference();

    /**
     * 弱引用，上层的应用信息，描述当前物理区域中运行了哪些应用容器
     *
     * @return {@link ConcurrentMap}
     */
    ConcurrentMap<String, Set<String>> pod();


    /**
     * 当前资源关联的所有存储信息，可主动提取存储信息
     *
     * @return {@link Set<String>}
     */
    Set<String> store();

    /**
     * 「云容器空间」Pod
     * 对应到 K8S 的基础软件空间，直接和 K8S 原生容器对接，该容器会包含一个所属，用来标识
     * 当前 Pod 的边界区域，而云容器和应用区域是多对一的处理
     *
     * @author <a href="http://www.origin-x.cn">Lang</a>
     */
    interface HPod extends Port {
        /**
         * 直接对应到 {@link HCube} 中的 identifier 信息，执行软关联
         *
         * @return {@link String}
         */
        String reference();

        /**
         * 底层的物理底座，描述当前容器运行在哪些物理区域中，直接对应内部类型
         * {@link HPlot}
         *
         * @return {@link Set}
         */
        Set<HPlot> zone();
    }
}
