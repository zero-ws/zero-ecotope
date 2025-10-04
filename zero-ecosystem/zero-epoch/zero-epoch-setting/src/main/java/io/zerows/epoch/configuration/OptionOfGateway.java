package io.zerows.epoch.configuration;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.platform.enums.app.ServerType;

/**
 * @author lang : 2024-04-20
 */
class OptionOfGateway extends OptionOfServerBase<HttpServerOptions> {
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
