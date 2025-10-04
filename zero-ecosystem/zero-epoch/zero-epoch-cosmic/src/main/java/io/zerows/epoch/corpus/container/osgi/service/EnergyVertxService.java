package io.zerows.epoch.corpus.container.osgi.service;

import io.vertx.core.Future;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.epoch.configuration.option.ClusterOptions;
import io.zerows.epoch.corpus.container.store.under.StoreVertx;
import io.zerows.epoch.corpus.container.uca.store.StubLinear;
import io.zerows.epoch.corpus.container.uca.store.StubVertx;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.support.fn.Fx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-30
 */
public class EnergyVertxService implements EnergyVertx {

    @Override
    public synchronized StubVertx ofVertx() {
        return StubVertx.of();
    }

    @Override
    public synchronized StubLinear ofLinear(final VertxComponent type) {
        return StubLinear.of(type);
    }

    @Override
    public Future<StoreVertx> startAsync(final NodeNetwork network) {
        // 外层传入 NodeNetwork
        final ClusterOptions clusterOptions = network.cluster();
        final ConcurrentMap<String, NodeVertx> vertxOptions = network.vertxOptions();


        /*
         * name-01 = VertxInstance
         * name-02 = VertxInstance
         */
        final ConcurrentMap<String, Future<RunVertx>> futureMap = new ConcurrentHashMap<>();
        final StubVertx service = this.ofVertx();
        vertxOptions.forEach((name, nodeVertx) ->
            futureMap.put(name, service.createAsync(nodeVertx, clusterOptions.isEnabled())));
        return Fx.combineM(futureMap)
            // 此处返回谁都可以，只是单纯为了其他位置可重用
            .compose(nil -> Future.succeededFuture(StoreVertx.of()));
    }
}
