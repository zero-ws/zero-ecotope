package io.zerows.core.web.client;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.app.KIntegration;
import io.zerows.epoch.common.shared.app.KIntegrationApi;
import org.apache.http.client.methods.HttpDelete;

public class DeleteRotator extends AbstractRotator {

    DeleteRotator(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegrationApi request, final JsonObject params) {
        final HttpDelete httpDelete = new HttpDelete(this.configPath(request, params));
        this.logger().info(INFO.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendUrl(httpDelete, request.getHeaders());
    }
}
