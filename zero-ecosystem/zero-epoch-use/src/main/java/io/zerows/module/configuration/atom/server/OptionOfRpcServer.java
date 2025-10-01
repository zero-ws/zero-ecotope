package io.zerows.module.configuration.atom.server;

import io.zerows.epoch.enums.app.ServerType;
import io.zerows.module.configuration.atom.option.RpcOptions;
import io.zerows.module.configuration.zdk.AbstractOptionBridge;
import io.zerows.module.configuration.zdk.OptionOfServer;

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
