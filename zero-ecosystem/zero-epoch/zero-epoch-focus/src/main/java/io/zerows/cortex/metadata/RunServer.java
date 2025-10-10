package io.zerows.cortex.metadata;

import io.vertx.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-10-10
 */
@Slf4j
public class RunServer implements RunInstance<HttpServer> {
    private final String name;

    public RunServer(final String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public HttpServer instance() {
        return null;
    }

    @Override
    public <RUN extends RunInstance<HttpServer>> RUN instance(final HttpServer httpServer) {
        return null;
    }

    @Override
    public <C> C config() {
        return null;
    }

    @Override
    public boolean isOk(final int hashCode) {
        return false;
    }
}
