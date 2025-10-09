package io.zerows.cortex.management;

import io.vertx.core.http.HttpServer;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.platform.exception._60050Exception501NotSupport;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-03
 */
class StoreServerAmbiguity extends AbstractAmbiguity implements StoreServer {
    private static final ConcurrentMap<String, RunServer> RUNNING = new ConcurrentHashMap<>();

    StoreServerAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    @Override
    public HttpServer server(final String serverKey) {
        final RunServer runServer = RUNNING.getOrDefault(serverKey, null);
        return Objects.isNull(runServer) ? null : runServer.instance();
    }

    @Override
    public HttpServer server() {
        if (1 != RUNNING.size()) {
            throw new _60050Exception501NotSupport(this.getClass());
        }
        final RunServer runServer = RUNNING.values().iterator().next();
        return Objects.isNull(runServer) ? null : runServer.instance();
    }

    @Override
    @SuppressWarnings("all")
    public StoreServer add(final RunServer runServer) {
        Objects.requireNonNull(runServer);
        if (runServer.isOk()) {
            RUNNING.put(runServer.name(), runServer);
        }
        return null;
    }

    @Override
    public StoreServer remove(final RunServer runServer) {
        return null;
    }


    @Override
    @SuppressWarnings("all")
    public StoreServer remove(final String name) {
        if (Ut.isNotNil(name)) {
            RUNNING.remove(name);
        }
        return this;
    }

    @Override
    public RunServer valueGet(final String name) {
        return RUNNING.getOrDefault(name, null);
    }

    @Override
    public Set<String> keys() {
        return RUNNING.keySet();
    }
}
