package io.zerows.core.web.container.uca.server;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.core.web.model.atom.running.RunServer;
import io.zerows.core.web.model.atom.running.RunVertx;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.atom.option.SockOptions;
import io.zerows.module.configuration.zdk.OptionOfServer;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2024-05-04
 */
class StubServerService extends AbstractAmbiguity implements StubServer {
    StubServerService(final Bundle bundle) {
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
