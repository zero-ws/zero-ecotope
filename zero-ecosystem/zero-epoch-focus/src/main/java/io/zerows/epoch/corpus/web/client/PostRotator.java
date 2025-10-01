package io.zerows.epoch.corpus.web.client;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.app.KIntegration;
import io.zerows.epoch.common.shared.app.KIntegrationApi;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class PostRotator extends AbstractRotator {

    PostRotator(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegrationApi request, final JsonObject params) {
        /*
         * HttpPost
         * */
        final HttpPost httpPost = new HttpPost(this.configPath(request, params));
        final StringEntity body = this.dataJson(params);
        this.logger().info(INFO.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendEntity(httpPost, body, request.getHeaders());
    }
}
