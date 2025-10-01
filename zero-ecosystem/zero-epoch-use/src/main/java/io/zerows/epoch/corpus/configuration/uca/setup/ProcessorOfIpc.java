package io.zerows.epoch.corpus.configuration.uca.setup;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.corpus.configuration.atom.NodeVertx;
import io.zerows.epoch.corpus.configuration.atom.option.RpcOptions;
import io.zerows.epoch.corpus.configuration.uca.transformer.RpcTransformer;
import io.zerows.epoch.corpus.configuration.zdk.Processor;
import io.zerows.epoch.corpus.configuration.zdk.Transformer;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfIpc implements Processor<NodeVertx, JsonArray> {
    private final transient Transformer<RpcOptions> rpcTransformer;

    ProcessorOfIpc() {
        this.rpcTransformer = new RpcTransformer();
    }

    @Override
    public void makeup(final NodeVertx target, final JsonArray setting) {
        this.logger().debug(INFO.V_BEFORE, KName.SERVER, ServerType.IPC, setting);

        Ut.itJArray(setting, (item, index) -> {
            final JsonObject configureJ = Ut.valueJObject(item, KName.CONFIG);

            final RpcOptions options = this.rpcTransformer.transform(configureJ);
            final String serverName = Ut.valueString(item, KName.NAME);
            target.optionServer(serverName, options);
        });
        if (Ut.isNotNil(setting)) {
            this.logger().info(INFO.V_AFTER, KName.SERVER, ServerType.IPC, setting);
        }
    }
}
