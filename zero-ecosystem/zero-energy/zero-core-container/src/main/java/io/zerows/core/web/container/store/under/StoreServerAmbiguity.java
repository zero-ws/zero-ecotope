package io.zerows.core.web.container.store.under;

import io.vertx.core.http.HttpServer;
import io.zerows.core.exception.web._501NotSupportException;
import io.zerows.core.util.Ut;
import io.zerows.core.web.model.atom.running.RunServer;
import io.zerows.module.metadata.zdk.AbstractAmbiguity;
import org.osgi.framework.Bundle;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2024-05-03
 */
class StoreServerAmbiguity extends AbstractAmbiguity implements StoreServer {
    private static final ConcurrentMap<String, RunServer> RUNNING = new ConcurrentHashMap<>();

    StoreServerAmbiguity(final Bundle bundle) {
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
            throw Ut.Bnd.failWeb(_501NotSupportException.class, this.getClass());
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
