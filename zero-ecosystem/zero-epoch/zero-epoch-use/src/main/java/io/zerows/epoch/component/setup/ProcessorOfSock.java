package io.zerows.epoch.component.setup;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.based.constant.KName;
import io.zerows.epoch.component.environment.MatureOn;
import io.zerows.epoch.component.transformer.TransformerSock;
import io.zerows.epoch.configuration.NodeVertx;
import io.zerows.epoch.configuration.option.SockOptions;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.epoch.program.Ut;
import io.zerows.epoch.sdk.options.Processor;
import io.zerows.epoch.sdk.options.Transformer;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfSock implements Processor<NodeVertx, JsonArray> {

    private transient final Transformer<SockOptions> sockTransformer;

    ProcessorOfSock() {
        this.sockTransformer = new TransformerSock();
    }


    @Override
    public void makeup(final NodeVertx target, final JsonArray setting) {
        this.logger().debug(ProcessorMessage.V_BEFORE, KName.SERVER, ServerType.SOCK, setting);


        Ut.itJArray(setting, (item, index) -> {


            JsonObject configureJ = Ut.valueJObject(item, KName.CONFIG);
            configureJ = MatureOn.envSock(configureJ, index);

            /*
             * 配置转换，构造结构
             * {
             *     "config": {},
             *     "websocket": {}
             * }
             */
            final JsonObject configData = new JsonObject();
            {
                configData.put(KName.CONFIG, configureJ);
                configData.put(KName.WEB_SOCKET, Ut.valueJObject(item, KName.WEB_SOCKET));
            }
            final SockOptions options = this.sockTransformer.transform(configData);
            final String serverName = Ut.valueString(item, KName.NAME);
            target.optionServer(serverName, options);
        });
        if (Ut.isNotNil(setting)) {
            this.logger().info(ProcessorMessage.V_AFTER, KName.SERVER, ServerType.SOCK, setting);
        }
    }
}
