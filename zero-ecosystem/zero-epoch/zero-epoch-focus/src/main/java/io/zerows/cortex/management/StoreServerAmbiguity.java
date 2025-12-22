package io.zerows.cortex.management;

import io.vertx.core.http.HttpServer;
import io.zerows.cortex.metadata.RunServer;
import io.zerows.platform.management.AbstractAmbiguity;
import io.zerows.specification.development.compiled.HBundle;
import io.zerows.support.Ut;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 此处修改成线程模型，每个线程都独享一个 {@link RunServer} 的实例
 *
 * @author lang : 2024-05-03
 */
class StoreServerAmbiguity extends AbstractAmbiguity implements StoreServer {
    private static final ConcurrentMap<String, RunServer> RUNNING = new ConcurrentHashMap<>();

    StoreServerAmbiguity(final HBundle bundle) {
        super(bundle);
    }

    private String serverKey(final String serverKey) {
        // 核心魔法：把线程名字加进 Key 里
        final String name = Thread.currentThread().getName();
        return name + "@" + serverKey;
    }

    @Override
    public HttpServer server(final String name) {
        final String serverKey = this.serverKey(name);
        final RunServer runServer = RUNNING.getOrDefault(serverKey, null);
        return Objects.isNull(runServer) ? null : runServer.instance();
    }

    @Override
    @SuppressWarnings("all")
    public StoreServer add(final RunServer runServer) {
        Objects.requireNonNull(runServer);
        if (runServer.isOk()) {
            final String serverKey = this.serverKey(runServer.name());
            RUNNING.put(serverKey, runServer);
        }
        return null;
    }

    @Override
    public StoreServer remove(final RunServer runServer) {
        if (Objects.nonNull(runServer)) {
            return this.remove(runServer.name());
        }
        return this;
    }


    @Override
    @SuppressWarnings("all")
    public StoreServer remove(final String name) {
        if (Ut.isNotNil(name)) {
            final Set<String> waitFor = new HashSet<>();
            for (String key : RUNNING.keySet()) {
                if (key.endsWith("@" + name)) {
                    waitFor.add(key);
                }
            }
            waitFor.forEach(RUNNING::remove);
        }
        return this;
    }

    @Override
    public RunServer valueGet(final String name) {
        final String serverKey = this.serverKey(name);
        return RUNNING.getOrDefault(serverKey, null);
    }

    @Override
    public Set<String> keys() {
        return RUNNING.keySet();
    }
}
