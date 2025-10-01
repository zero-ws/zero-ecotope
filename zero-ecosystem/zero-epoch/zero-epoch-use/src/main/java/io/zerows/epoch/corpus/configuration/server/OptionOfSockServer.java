package io.zerows.epoch.corpus.configuration.server;

import io.zerows.epoch.corpus.configuration.option.SockOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.epoch.sdk.options.AbstractOptionBridge;
import io.zerows.epoch.sdk.options.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
class OptionOfSockServer extends AbstractOptionBridge<SockOptions> {

    private OptionOfSockServer(final String name) {
        super(name);
    }

    static OptionOfServer<SockOptions> of(final String name, final SockOptions options) {
        return new OptionOfSockServer(name).options(options);
    }

    @Override
    public ServerType type() {
        return ServerType.SOCK;
    }
}
