package io.zerows.cosmic;

import io.r2mo.SourceReflect;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxBuilder;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.spi.cluster.ClusterManager;
import io.zerows.cortex.extension.CodecEnvelop;
import io.zerows.cortex.management.StoreVertx;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.epoch.web.Envelop;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author lang : 2024-04-30
 */
@Slf4j
class StubVertxService extends AbstractAmbiguity implements StubVertx {

    StubVertxService(final HBundle bundle) {
        super(bundle);
    }

    StubVertxService() {
        super(null);
    }

    @Override
    public synchronized Future<RunVertx> createAsync(final NodeVertx nodeVertx, final boolean clustered) {
        Objects.requireNonNull(nodeVertx);
        final VertxBuilder builder = Vertx.builder();
        if (clustered) {
            // 集群模式创建
            final NodeNetwork network = nodeVertx.networkRef();
            final ClusterManager manager = network.cluster().getClusterManager();
            log.info("[ ZERO ] 当前应用程序正在集群模式下运行，管理器 = {}，节点为 {}，isActive = {}。",
                manager.getClass().getName(), manager.getNodeId(), manager.isActive());
            return builder
                .withClusterManager(manager)
                .with(nodeVertx.vertxOptions()).buildClustered()
                .compose(vertx -> this.createFinished(nodeVertx, vertx));
        } else {
            // 非集群模式创建
            return Future.succeededFuture(builder.with(nodeVertx.vertxOptions()).build())
                .compose(vertx -> this.createFinished(nodeVertx, vertx));
        }
    }

    private Future<RunVertx> createFinished(final NodeVertx config, final Vertx vertx) {
        // 完善 Vertx 实例
        final EventBus eventBus = vertx.eventBus();
        eventBus.registerDefaultCodec(Envelop.class, SourceReflect.singleton(CodecEnvelop.class));
        log.info("[ ZERO ] 注册编解码器：{}", CodecEnvelop.class.getName());

        // 核心 RunVertx 实例
        final RunVertx runVertx = new RunVertx(config.name());
        this.add(runVertx.name(), runVertx
            .instance(vertx)           // 运行实例
            .config(config)            // 运行配置
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
