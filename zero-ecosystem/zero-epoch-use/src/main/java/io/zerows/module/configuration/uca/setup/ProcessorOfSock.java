package io.zerows.module.configuration.uca.setup;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.atom.option.SockOptions;
import io.zerows.module.configuration.uca.transformer.SockTransformer;
import io.zerows.module.configuration.zdk.Processor;
import io.zerows.module.configuration.zdk.Transformer;
import io.zerows.module.metadata.uca.environment.MatureOn;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfSock implements Processor<NodeVertx, JsonArray> {

    private transient final Transformer<SockOptions> sockTransformer;

    ProcessorOfSock() {
        this.sockTransformer = new SockTransformer();
    }


    @Override
    public void makeup(final NodeVertx target, final JsonArray setting) {
        this.logger().debug(INFO.V_BEFORE, KName.SERVER, ServerType.SOCK, setting);


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
            this.logger().info(INFO.V_AFTER, KName.SERVER, ServerType.SOCK, setting);
        }
    }
}
