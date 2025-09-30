package io.zerows.core.web.client;

import io.vertx.core.json.JsonObject;
import io.zerows.common.app.KIntegration;
import io.zerows.common.app.KIntegrationApi;
import org.apache.http.client.methods.HttpGet;

public class GetRotator extends AbstractRotator {

    GetRotator(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegrationApi request, final JsonObject params) {
        /*
         * Turn On atom workflow when integration is `debug`
         */
        final HttpGet httpGet = new HttpGet(this.configPath(request, params));
        this.logger().info(INFO.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendUrl(httpGet, request.getHeaders());
    }
}
