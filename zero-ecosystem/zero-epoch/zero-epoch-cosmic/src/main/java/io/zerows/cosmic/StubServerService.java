package io.zerows.cosmic;

import io.r2mo.typed.exception.web._404NotFoundException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.shareddata.Lock;
import io.zerows.cortex.management.StoreServer;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.cortex.metadata.RunVertx;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.configuration.NodeNetwork;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.Objects;

/**
 * @author lang : 2024-05-04
 */
class StubServerService extends AbstractAmbiguity implements StubServer {
    StubServerService(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Future<RunServer> createAsync(final RunVertx runVertx) {
        final Promise<RunServer> promise = Promise.promise();
        final Vertx vertxRef = runVertx.instance();
        if (Objects.isNull(vertxRef)) {
            throw new _404NotFoundException("[ ZERO ] Vertx 实例未找到，无法创建 HttpServer");
        }


        /*
         * 一对一绑定，Vertx 的名称用来处理 Server 名称，对应到
         * vertx:
         *     application:
         *         name: demo-vertx
         * 微服务模式下为服务名
         */
        final NodeVertx config = runVertx.config();
        final NodeNetwork network = config.networkRef();


        final HttpServerOptions serverOptions = network.server();
        final String serverName = serverOptions.getHost() + ":" + serverOptions.getPort();

        /*
         * 根据是否集群场景考虑使用 Vertx 中的本地锁 / 分布式锁
         */
        final boolean isCluster = Objects.nonNull(network.cluster());
        final String keyLock = "server:create:" + serverName;
        final Future<Lock> lockFuture;
        if (isCluster) {
            lockFuture = vertxRef.sharedData().getLockWithTimeout(keyLock, 2_000L);
        } else {
            lockFuture = vertxRef.sharedData().getLocalLockWithTimeout(keyLock, 2_000L);
        }
        lockFuture.onFailure(promise::fail).onSuccess(lock -> {
            try {
                // === 临界区：查→建→放 ===
                RunServer runServer = StoreServer.of().valueGet(serverName);
                if (Objects.isNull(runServer)) {
                    final HttpServer server = vertxRef.createHttpServer(serverOptions);
                    /* 构造 RunServer 实例 */
                    runServer = new RunServer(serverName);
                    final SockOptions sockOptions = network.sock();
                    runServer
                        .config(sockOptions)
                        .config(serverOptions)
                        .refRunVertx(runVertx)
                        .instance(server)
                        .build();


                    StoreServer.of().add(runServer);
                }
                promise.complete(runServer);
            } catch (final Throwable ex) {
                promise.fail(ex);
            } finally {
                lock.release();
            }
        });
        return promise.future();
    }
}
