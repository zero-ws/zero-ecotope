package io.zerows.cosmic.plugins.client;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KIntegration;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class RotatorPost extends RotatorBase {

    RotatorPost(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegration.Api request, final JsonObject params) {
        /*
         * HttpPost
         * */
        final HttpPost httpPost = new HttpPost(this.configPath(request, params));
        final StringEntity body = this.dataJson(params);
        this.logger().info(Emitter.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendEntity(httpPost, body, request.getHeaders());
    }
}
