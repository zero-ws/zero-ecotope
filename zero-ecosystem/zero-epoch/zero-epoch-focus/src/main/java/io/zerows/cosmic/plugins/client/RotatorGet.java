package io.zerows.cosmic.plugins.client;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KIntegration;
import org.apache.http.client.methods.HttpGet;

public class RotatorGet extends RotatorBase {

    RotatorGet(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegration.Api request, final JsonObject params) {
        /*
         * Turn On atom workflow when integration is `debug`
         */
        final HttpGet httpGet = new HttpGet(this.configPath(request, params));
        this.logger().info(Emitter.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendUrl(httpGet, request.getHeaders());
    }
}
