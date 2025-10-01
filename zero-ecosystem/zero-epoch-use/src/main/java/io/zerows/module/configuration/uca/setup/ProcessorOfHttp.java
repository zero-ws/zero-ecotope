package io.zerows.module.configuration.uca.setup;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.enums.app.ServerType;
import io.zerows.core.constant.KName;
import io.zerows.core.util.Ut;
import io.zerows.module.configuration.atom.NodeVertx;
import io.zerows.module.configuration.uca.transformer.HttpServerTransformer;
import io.zerows.module.configuration.zdk.Processor;
import io.zerows.module.configuration.zdk.Transformer;
import io.zerows.module.metadata.uca.environment.MatureOn;

/**
 * @author lang : 2024-04-20
 */
class ProcessorOfHttp implements Processor<NodeVertx, JsonArray> {
    private final transient Transformer<HttpServerOptions> httpTransformer;

    ProcessorOfHttp() {
        this.httpTransformer = Ut.singleton(HttpServerTransformer.class);
    }

    @Override
    public void makeup(final NodeVertx target, final JsonArray setting) {
        // 此处保证所有的配置都为同一种类型的服务器配置
        this.logger().debug(INFO.V_BEFORE, KName.SERVER, this.typeOfHttp(), setting);


        // 验证已通过，直接构造 HttpServerOptions
        Ut.itJArray(setting, (item, index) -> {


            // 新版不做 isServer 的判断，外层已验证
            JsonObject configureJ = Ut.valueJObject(item, KName.CONFIG);
            /*
             * 此处是直接使用环境变量处理服务器中的 HOST / PORT，用于容器级的发布处理
             * 多服务器模式下，Server 有更多可用的环境变量，后缀为索引值（0不带索引）
             *
             * Z_HOST=HOST0, HOST1, HOST2 etc.
             */
            configureJ = MatureOn.envApi(configureJ, index);

            final HttpServerOptions options = this.httpTransformer.transform(configureJ);
            final String serverName = Ut.valueString(item, KName.NAME);
            target.optionServer(serverName, this.typeOfHttp(), options);
        });
        if (Ut.isNotNil(setting)) {
            this.logger().info(INFO.V_AFTER, KName.SERVER, this.typeOfHttp(), setting);
        }
    }

    protected ServerType typeOfHttp() {
        return ServerType.HTTP;
    }
}
