package io.zerows.epoch.corpus.configuration.atom.server;

import io.vertx.core.http.HttpServerOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.epoch.corpus.configuration.zdk.AbstractOptionBridge;
import io.zerows.epoch.corpus.configuration.zdk.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
class OptionOfHttpServer extends AbstractOptionBridge<HttpServerOptions> {
    private OptionOfHttpServer(final String name) {
        super(name);
    }

    static OptionOfServer<HttpServerOptions> of(final String name, final HttpServerOptions options) {
        return new OptionOfHttpServer(name).options(options);
    }

    @Override
    public ServerType type() {
        return ServerType.HTTP;
    }
}
