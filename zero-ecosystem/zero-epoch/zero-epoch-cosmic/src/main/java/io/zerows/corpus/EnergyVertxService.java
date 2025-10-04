package io.zerows.corpus;

import io.vertx.core.Future;
import io.zerows.corpus.container.StubLinear;
import io.zerows.corpus.container.StubVertx;
import io.zerows.corpus.management.StoreVertx;
import io.zerows.epoch.basicore.NodeNetwork;
import io.zerows.epoch.basicore.NodeVertx;
import io.zerows.epoch.basicore.option.ClusterOptions;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.platform.enums.VertxComponent;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.fn.Fx;

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
    public synchronized StubLinear ofLinear(final HBundle bundle, final VertxComponent type) {
        return StubLinear.of(bundle, type);
    }

    @Override
    public Future<StoreVertx> startAsync(final HBundle bundle, final NodeNetwork network) {
        // 外层传入 NodeNetwork
        final ClusterOptions clusterOptions = network.cluster();
        final ConcurrentMap<String, NodeVertx> vertxOptions = network.vertxOptions();


        /*
         * name-01 = VertxInstance
         * name-02 = VertxInstance
         */
        final ConcurrentMap<String, Future<RunVertx>> futureMap = new ConcurrentHashMap<>();
        final StubVertx service = this.ofVertx(bundle);
        vertxOptions.forEach((name, nodeVertx) ->
            futureMap.put(name, service.createAsync(nodeVertx, clusterOptions.isEnabled())));
        return Fx.combineM(futureMap)
            // 此处返回谁都可以，只是单纯为了其他位置可重用
            .compose(nil -> Future.succeededFuture(StoreVertx.of()));
    }
}
