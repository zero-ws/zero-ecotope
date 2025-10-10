package io.zerows.cosmic;

import io.vertx.core.Future;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.cosmic.bootstrap.Linear;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.fn.Fx;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-30
 */
public class EnergyVertxService implements EnergyVertx {

    @Override
    public synchronized StubVertx ofVertx(final HBundle bundle) {
        return StubVertx.of(bundle);
    }

    @Override
    public synchronized Linear ofLinear(final HBundle bundle, final VertxComponent type) {
        return Linear.of(bundle, type);
    }

    @Override
    public Future<StoreVertx> startAsync(final HBundle bundle, final NodeNetwork network) {
        final ClusterOptions clusterOptions = network.cluster();
        final boolean isClustered = Objects.nonNull(clusterOptions) && clusterOptions.isEnabled();


        /*
         * name-01 = VertxInstance
         * name-02 = VertxInstance
         */
        final ConcurrentMap<String, NodeVertx> vertxInstances = network.vertxNodes();

        
        final ConcurrentMap<String, Future<RunVertx>> futureMap = new ConcurrentHashMap<>();
        vertxInstances.forEach((name, nodeVertx) ->
            futureMap.put(name, this.ofVertx(bundle).createAsync(nodeVertx, isClustered)));
        return Fx.combineM(futureMap)
            // 此处返回谁都可以，只是单纯为了其他位置可重用
            .compose(nil -> Future.succeededFuture(StoreVertx.of()));
    }
}
