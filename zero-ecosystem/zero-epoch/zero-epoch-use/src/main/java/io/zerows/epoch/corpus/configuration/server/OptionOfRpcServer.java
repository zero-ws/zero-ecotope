package io.zerows.epoch.corpus.configuration.server;

import io.zerows.epoch.corpus.configuration.option.RpcOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.epoch.sdk.options.AbstractOptionBridge;
import io.zerows.epoch.sdk.options.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
class OptionOfRpcServer extends AbstractOptionBridge<RpcOptions> {

    private OptionOfRpcServer(final String name) {
        super(name);
    }

    static OptionOfServer<RpcOptions> of(final String name, final RpcOptions options) {
        return new OptionOfRpcServer(name).options(options);
    }

    @Override
    public ServerType type() {
        return ServerType.IPC;
    }
}
