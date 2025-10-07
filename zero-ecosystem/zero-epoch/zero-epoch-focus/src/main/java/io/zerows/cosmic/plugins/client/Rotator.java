package io.zerows.cosmic.plugins.client;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.zerows.platform.annotations.meta.Memory;
import io.zerows.platform.metadata.KIntegration;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/*
 * Rotator for http request of major for methods:
 *
 * - POST
 * - GET
 * - PUT
 * - DELETE
 */
public interface Rotator {
    @Memory(Rotator.class)
    Cc<Integer, Rotator> CC_ROTATOR = Cc.open();
    ConcurrentMap<HttpMethod, Function<KIntegration, Rotator>> POOL_ROTATOR_FN =
        new ConcurrentHashMap<HttpMethod, Function<KIntegration, Rotator>>() {
            {
                this.put(HttpMethod.GET, GetRotator::new);
                this.put(HttpMethod.DELETE, DeleteRotator::new);
                this.put(HttpMethod.POST, PostRotator::new);
                this.put(HttpMethod.PUT, PutRotator::new);
            }
        };

    /*
     * The rotator could bind to HttpClient ( core )
     */
    Rotator bind(CloseableHttpClient client);

    /*
     * Request data with `InJson` parameters, get string response
     */
    String request(KIntegration.Api request, JsonObject params);
}
