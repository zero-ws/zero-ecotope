package io.zerows.cosmic;

import io.vertx.core.Future;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cosmic.bootstrap.Linear;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.specification.development.compiled.HBundle;

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
        //        // 外层传入 NodeNetwork
        //        final ClusterOptions clusterOptions = network.cluster();
        //        final ConcurrentMap<String, NodeVertxLegacy> vertxOptions = network.vertxNodes();
        //
        //
        //        /*
        //         * name-01 = VertxInstance
        //         * name-02 = VertxInstance
        //         */
        //        final ConcurrentMap<String, Future<RunVertxLegacy>> futureMap = new ConcurrentHashMap<>();
        //        final StubVertx service = this.ofVertx(bundle);
        //        vertxOptions.forEach((name, nodeVertx) ->
        //            futureMap.put(name, service.createAsync(nodeVertx, clusterOptions.isEnabled())));
        //        return Fx.combineM(futureMap)
        //            // 此处返回谁都可以，只是单纯为了其他位置可重用
        //            .compose(nil -> Future.succeededFuture(StoreVertx.of()));
        return null;
    }
}
