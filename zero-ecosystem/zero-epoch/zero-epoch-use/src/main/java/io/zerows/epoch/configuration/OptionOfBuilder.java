package io.zerows.epoch.configuration;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.configuration.option.RpcOptions;
import io.zerows.epoch.configuration.option.SockOptions;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.sdk.environment.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
public class OptionOfBuilder {


    public static OptionOfServer<HttpServerOptions> ofHttp(final String name,
                                                           final ServerType type,
                                                           final HttpServerOptions options) {
        return switch (type) {
            case HTTP -> OptionOfHttpServer.of(name, options);
            case API -> OptionOfGateway.of(name, options);
            case RX -> OptionOfRxServer.of(name, options);
            default -> throw new IllegalArgumentException("Unknown server type: " + type);
        };
    }

    public static OptionOfServer<SockOptions> ofSock(final String name, final SockOptions options) {
        return OptionOfSockServer.of(name, options);
    }

    public static OptionOfServer<RpcOptions> ofRpc(final String name, final RpcOptions options) {
        return OptionOfRpcServer.of(name, options);
    }
}
