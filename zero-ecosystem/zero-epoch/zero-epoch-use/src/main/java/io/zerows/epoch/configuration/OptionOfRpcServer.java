package io.zerows.epoch.configuration;

import io.zerows.epoch.configuration.option.RpcOptions;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.epoch.sdk.environment.OptionOfServerBase;
import io.zerows.epoch.sdk.environment.OptionOfServer;

/**
 * @author lang : 2024-04-20
 */
class OptionOfRpcServer extends OptionOfServerBase<RpcOptions> {

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
