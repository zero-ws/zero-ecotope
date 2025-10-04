package io.zerows.corpus;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.basicore.NodeVertx;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.configuration.OptionOfServer;
import io.zerows.epoch.corpus.model.running.RunServer;
import io.zerows.epoch.corpus.model.running.RunVertx;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.sdk.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-04
 */
class StubServerService extends AbstractAmbiguity implements StubServer {
    StubServerService(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public Set<RunServer> createAsync(final RunVertx runVertx) {

        final Vertx vertxRef = runVertx.instance();
        if (Objects.isNull(vertxRef)) {
            return Set.of();
        }

        final NodeVertx config = runVertx.config();
        final Set<String> servers = config.optionServers(ServerType.HTTP);
        final Set<RunServer> serverSet = new HashSet<>();

        servers.stream().map(config::<HttpServerOptions>optionServer).forEach(option -> {
            final OptionOfServer<SockOptions> optionOfSock = config.findSock(option);
            /* 提取 HTTP 服务器配置 */
            final HttpServerOptions optionsHttp = option.options();
            final HttpServer server = vertxRef.createHttpServer(optionsHttp);
            /* 构造 RunServer */
            serverSet.add(new RunServer(option.name())
                .config(option)
                .configSock(optionOfSock)
                .refRunVertx(runVertx)
                .instance(server).build());
        });
        return serverSet;
    }
}
