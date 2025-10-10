package io.zerows.cosmic;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.zerows.component.log.OLog;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.cosmic.bootstrap.Linear;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-04-30
 */
public interface EnergyVertx {
    /**
     * 此处的 {@link EnergyVertx} 实际是 Factory 模式，主要针对 Vertx 实例的创建和管理，它内置结构如：
     * <pre><code>
     *     {@link EnergyVertx}
     *         Bundle 01 = {@link StubVertx}
     *                      {@link StoreVertx} = {@link Vertx} x N
     *                         and
     *                      {@link RunVertx} = {@link Vertx} x 1
     *         Bundle 02 = {@link StubVertx}
     * </code></pre>
     * 但是此处的 {@link StubVertx} 不对外，仅用于 Bundle 内部管理，外部只能通过当前服务获取对应的 {@link StubVertx} 引用
     *
     * @return {@link StubVertx}
     */
    StubVertx ofVertx(HBundle bundle);


    Linear ofLinear(HBundle bundle, VertxComponent type);

    /**
     * 网络启动专用，用于启动如下基础结构（特殊方法，通常这个接口不带此方法）
     * <pre><code>
     *     实例结构：
     *     Cluster
     *         name 01 = VertxInstance {@link Vertx}
     *         name 02 = VertxInstance
     *     配置结构：
     *     Cluster -> {@link NodeNetwork}
     *         name 01 = {@link NodeVertx}
     *         name 02 = {@link NodeVertx}
     * </code></pre>
     * 注意整体结构和 {@link Vertx} 之间的关系
     * <pre><code>
     *     {@link StoreVertx}      {@link RunVertx}
     *       Vertx x N            Vertx x 1
     * </code></pre>
     * 此处传入的配置实例是 {@link NodeNetwork}，所以启动的最终 {@link Vertx} 实例是多个 x N，所以此处
     *
     * @param network {@link NodeNetwork}
     *
     * @return {@link StoreVertx}
     */
    Future<StoreVertx> startAsync(HBundle bundle, NodeNetwork network);

    default OLog logger() {
        return Ut.Log.vertx(this.getClass());
    }
}
