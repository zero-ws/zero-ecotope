package io.zerows.epoch.corpus.web.client;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.common.shared.app.KIntegrationApi;
import org.apache.http.impl.client.CloseableHttpClient;

/*
 * Rotator for http request of major for methods:
 *
 * - POST
 * - GET
 * - PUT
 * - DELETE
 */
public interface Rotator {
    /*
     * The rotator could bind to HttpClient ( core )
     */
    Rotator bind(CloseableHttpClient client);

    /*
     * Request data with `InJson` parameters, get string response
     */
    String request(KIntegrationApi request, JsonObject params);
}
