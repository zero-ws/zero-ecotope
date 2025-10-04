package io.zerows.epoch.configuration;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.basicore.option.SockOptions;
import io.zerows.epoch.constant.KName;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class TransformerSock implements Transformer<SockOptions> {
    @Override
    public SockOptions transform(final JsonObject config) {
        if (Objects.isNull(config)) {
            return new SockOptions();
        }
        /*
         * websocket:       ( SockOptions )
         * config:          ( HttpServerOptions )
         */
        final JsonObject websockJ = Ut.valueJObject(config, KName.WEB_SOCKET);
        final SockOptions options = Ut.deserialize(websockJ, SockOptions.class);

        /*
         * Bind the HttpServerOptions to SockOptions for future usage
         * 此处结合 WebSocket 的配置结构执行转换，生成对应的 HttpServerOptions
         *
         */
        final JsonObject optionJ = Ut.valueJObject(config, KName.CONFIG);
        final HttpServerOptions serverOptions = new HttpServerOptions(optionJ);

        options.options(serverOptions);
        return options;
    }
}
