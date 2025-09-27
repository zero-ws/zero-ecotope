package io.zerows.core.web.client;

import io.zerows.common.app.KIntegration;
import io.zerows.common.app.KIntegrationApi;
import io.vertx.core.json.JsonObject;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

public class PutRotator extends AbstractRotator {

    PutRotator(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegrationApi request, final JsonObject params) {
        /*
         * HttpPut
         */
        final HttpPut httpPut = new HttpPut(this.configPath(request, params));
        final StringEntity body = this.dataJson(params);
        this.logger().info(INFO.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendEntity(httpPut, body, request.getHeaders());
    }
}
