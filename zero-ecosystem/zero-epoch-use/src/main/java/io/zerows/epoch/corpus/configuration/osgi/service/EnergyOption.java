package io.zerows.epoch.corpus.configuration.osgi.service;

import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.configuration.atom.NodeNetwork;
import io.zerows.epoch.corpus.configuration.atom.NodeVertx;
import io.zerows.epoch.corpus.metadata.uca.logging.OLog;
import io.zerows.epoch.corpus.metadata.zdk.service.ServiceContext;

import java.util.Set;

/**
 * 配置管理器，用于管理所有配置专用，每个 Bundle 只能拥有一个 {@link NodeNetwork} 实例，这是模块的边界，它的内置结构如：
 * <pre><code>
 *     NodeNetwork x 1      = Cluster x 1
 *     NodeVertx x N        = name -> NodeVertx
 *     每个 NodeVertx 的构造  = 参考 {@link NodeVertx}
 * </code></pre>
 *
 * @author lang : 2024-04-28
 */
public interface EnergyOption {

    NodeNetwork network(ServiceContext context);

    Set<NodeVertx> vertx(ServiceContext context);

    default OLog logger() {
        return Ut.Log.energy(this.getClass());
    }
}
