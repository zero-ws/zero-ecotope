package io.zerows.core.web.container.osgi.service;

import io.vertx.core.Future;
import io.zerows.core.fn.FnZero;
import io.zerows.core.util.Ut;
import io.zerows.core.web.container.store.under.StoreVertx;
import io.zerows.core.web.container.uca.store.StubLinear;
import io.zerows.core.web.container.uca.store.StubVertx;
import io.zerows.core.web.model.atom.running.RunVertx;
import io.zerows.epoch.enums.VertxComponent;
import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.atom.option.ClusterOptions;
import org.osgi.framework.Bundle;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-04-30
 */
public class EnergyVertxService implements EnergyVertx {

    @Override
    public synchronized StubVertx ofVertx(final Bundle bundle) {
        final String cacheKey = Ut.Bnd.keyCache(bundle, EnergyVertxService.class);
        this.logger().info("StubVertx service fetched by key = {}", cacheKey);
        return StubVertx.of(bundle);
    }

    @Override
    public synchronized StubLinear ofLinear(final Bundle bundle, final VertxComponent type) {
        return StubLinear.of(bundle, type);
    }

    @Override
    public Future<StoreVertx> startAsync(final Bundle bundle, final NodeNetwork network) {
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
        return FnZero.combineM(futureMap)
            // 此处返回谁都可以，只是单纯为了其他位置可重用
            .compose(nil -> Future.succeededFuture(StoreVertx.of(bundle)));
    }
}
