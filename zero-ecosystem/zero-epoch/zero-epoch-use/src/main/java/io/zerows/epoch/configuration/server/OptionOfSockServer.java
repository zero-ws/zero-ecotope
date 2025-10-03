package io.zerows.epoch.configuration.server;

import io.zerows.epoch.configuration.option.SockOptions;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.epoch.sdk.environment.OptionOfServerBase;
import io.zerows.epoch.sdk.environment.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
class OptionOfSockServer extends OptionOfServerBase<SockOptions> {

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
