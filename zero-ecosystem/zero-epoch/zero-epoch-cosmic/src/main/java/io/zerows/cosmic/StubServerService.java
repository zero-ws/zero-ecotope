package io.zerows.cosmic;

import io.r2mo.typed.exception.web._404NotFoundException;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
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
        try {
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
            ex.printStackTrace();
            promise.fail(ex);
        }
        return promise.future();
    }
}
