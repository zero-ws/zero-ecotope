package io.zerows.epoch.configuration;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.zerows.sdk.environment.Transformer;
import io.zerows.support.Ut;

import java.util.Objects;

/**
 * @author lang : 2024-04-20
 */
public class TransformerHttpServer implements Transformer<HttpServerOptions> {
    /**
     * 输入服务器配置信息
     * <pre><code>
     * - name:
     *   type:
     *   config:
     *     port: ??
     *     host: 0.0.0.0
     *     ssl: true
     *     useAlpn: true
     *     keyStoreOptions:
     *        type: jks
     *        path: ????
     *        password: ????
     * </code></pre>
     *
     * @param input 服务器配置数据
     *
     * @return 构造好的 HTTP Server
     */
    @Override
    public HttpServerOptions transform(final JsonObject input) {
        // port = 80 issue
        final JsonObject config = Ut.valueJObject(input);
        if (Ut.isNil(config)) {
            return new HttpServerOptions();
        }
        assert Objects.nonNull(config) : "`config` is not null";
        return new HttpServerOptions(config);
    }
}
