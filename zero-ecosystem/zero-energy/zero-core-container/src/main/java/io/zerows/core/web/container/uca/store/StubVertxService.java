package io.zerows.core.web.container.uca.store;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxBuilder;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.spi.cluster.ClusterManager;
import io.zerows.core.util.Ut;
import io.zerows.core.web.container.store.under.StoreVertx;
import io.zerows.core.web.model.atom.running.RunVertx;
import io.zerows.core.web.model.commune.Envelop;
import io.zerows.core.web.model.uca.codec.EnvelopCodec;
import io.zerows.module.configuration.atom.NodeNetwork;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.Objects;

/**
 * @author lang : 2024-04-30
 */
class StubVertxService extends AbstractAmbiguity implements StubVertx {

    StubVertxService(final Bundle bundle) {
        super(bundle);
    }

    @Override
    public synchronized Future<RunVertx> createAsync(final NodeVertx nodeVertx, final boolean clustered) {
        Objects.requireNonNull(nodeVertx);
        final VertxBuilder builder = Vertx.builder();
        if (clustered) {
            // 集群模式创建
            final NodeNetwork network = nodeVertx.belongTo();
            final ClusterManager manager = network.cluster().getManager();
            this.logger().info("Current app is running in cluster mode, manager = {0} on node {1} with isActive = {2}.",
                manager.getClass().getName(), manager.getNodeId(), manager.isActive());
            return builder
                .withClusterManager(manager)
                .with(nodeVertx.optionVertx()).buildClustered()
                .compose(vertx -> this.createFinished(nodeVertx, vertx));
        } else {
            // 非集群模式创建
            return Future.succeededFuture(builder.with(nodeVertx.optionVertx()).build())
                .compose(vertx -> this.createFinished(nodeVertx, vertx));
        }
    }

    private Future<RunVertx> createFinished(final NodeVertx config, final Vertx vertx) {
        // 完善 Vertx 实例
        final EventBus eventBus = vertx.eventBus();
        eventBus.registerDefaultCodec(Envelop.class, Ut.singleton(EnvelopCodec.class));

        // 核心 RunVertx 实例
        final RunVertx runVertx = new RunVertx(config.name());
        this.add(runVertx.name(), runVertx
            .instance(vertx)           // 运行实例
            .config(config)         // 运行配置
        );
        return Future.succeededFuture(runVertx);
    }

    @Override
    public Vertx get(final String name) {
        final StoreVertx doVertx = StoreVertx.of(this.caller());
        return doVertx.vertx(name);
    }

    @Override
    public StubVertx add(final String name, final RunVertx runVertx) {
        Objects.requireNonNull(runVertx);
        final StoreVertx doVertx = StoreVertx.of(this.caller());
        if (runVertx.isOk()) {
            doVertx.add(runVertx);
        }
        return this;
    }

    @Override
    public StubVertx remove(final String name) {
        if (Ut.isNil(name)) {
            return this;
        }
        final StoreVertx doVertx = StoreVertx.of(this.caller());
        doVertx.remove(name);
        return this;
    }
}
