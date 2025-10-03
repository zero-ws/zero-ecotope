package io.zerows.epoch.corpus.web.client;

import io.vertx.core.MultiMap;
import io.zerows.metadata.app.KIntegration;
import io.zerows.epoch.corpus.security.token.WebToken;
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
        final WebToken token = this.token();
        if (Objects.nonNull(token)) {
            headers.add(HttpHeaders.AUTHORIZATION, this.token().authorization());
        }
        return headers;
    }

    public WebToken token() {
        return null;
    }
}
