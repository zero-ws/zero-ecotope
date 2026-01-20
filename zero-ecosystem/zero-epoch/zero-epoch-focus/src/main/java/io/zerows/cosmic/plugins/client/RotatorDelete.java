package io.zerows.cosmic.plugins.client;

import io.vertx.core.json.JsonObject;
import io.zerows.platform.metadata.KIntegration;
import org.apache.http.client.methods.HttpDelete;

public class RotatorDelete extends RotatorBase {

    RotatorDelete(final KIntegration integration) {
        super(integration);
    }

    @Override
    public String request(final KIntegration.Api request, final JsonObject params) {
        final HttpDelete httpDelete = new HttpDelete(this.configPath(request, params));
        this.logger().info(Emitter.HTTP_REQUEST, request.getPath(), request.getMethod(), params);
        return this.sendUrl(httpDelete, request.getHeaders());
    }
}
