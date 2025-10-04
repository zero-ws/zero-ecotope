package io.zerows.epoch.configuration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.configuration.option.RpcOptions;
import io.zerows.epoch.constant.KName;
import io.zerows.platform.enums.app.ServerType;
import io.zerows.sdk.environment.Processor;
import io.zerows.sdk.environment.Transformer;
import io.zerows.support.Ut;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfIpc implements Processor<NodeVertx, JsonArray> {
    private final transient Transformer<RpcOptions> rpcTransformer;

    ProcessorOfIpc() {
        this.rpcTransformer = new TransformerRpc();
    }

    @Override
    public void makeup(final NodeVertx target, final JsonArray setting) {
        this.logger().debug(ProcessorMessage.V_BEFORE, KName.SERVER, ServerType.IPC, setting);

        Ut.itJArray(setting, (item, index) -> {
            final JsonObject configureJ = Ut.valueJObject(item, KName.CONFIG);

            final RpcOptions options = this.rpcTransformer.transform(configureJ);
            final String serverName = Ut.valueString(item, KName.NAME);
            target.optionServer(serverName, options);
        });
        if (Ut.isNotNil(setting)) {
            this.logger().info(ProcessorMessage.V_AFTER, KName.SERVER, ServerType.IPC, setting);
        }
    }
}
