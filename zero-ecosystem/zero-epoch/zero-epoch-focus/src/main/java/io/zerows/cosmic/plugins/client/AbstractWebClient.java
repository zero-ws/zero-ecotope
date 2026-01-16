package io.zerows.cosmic.plugins.client;

import io.vertx.core.MultiMap;
import io.zerows.platform.metadata.KIntegration;
import org.apache.http.HttpHeaders;

import java.util.Objects;

public abstract class AbstractWebClient {
    protected final transient Emitter emitter;
    protected final transient KIntegration integration;

    public AbstractWebClient(final KIntegration integration) {
        this.emitter = Emitter.create(integration);
        this.integration = integration;
    }

    protected Emitter emitter() {
        return this.emitter;
    }

    protected MultiMap headers() {
        final MultiMap headers = MultiMap.caseInsensitiveMultiMap();
        final String token = this.token();
        if (Objects.nonNull(token)) {
            headers.add(HttpHeaders.AUTHORIZATION, token);
        }
        return headers;
    }

    public String token() {
        return null;
    }
}
