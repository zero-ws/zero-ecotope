package io.zerows.module.configuration.atom.server;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.module.configuration.zdk.AbstractOptionBridge;
import io.zerows.module.configuration.zdk.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
class OptionOfGateway extends AbstractOptionBridge<HttpServerOptions> {
    private OptionOfGateway(final String name) {
        super(name);
    }

    static OptionOfServer<HttpServerOptions> of(final String name, final HttpServerOptions options) {
        return new OptionOfGateway(name).options(options);
    }

    @Override
    public ServerType type() {
        return ServerType.API;
    }
}
