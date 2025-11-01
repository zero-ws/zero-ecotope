package io.zerows.epoch.bootplus.extension.uca.graphic;

import io.vertx.core.Future;
import io.zerows.boot.extension.util.Ox;
import io.zerows.component.log.LogOf;
import io.zerows.epoch.constant.KWeb;
import io.zerows.plugins.store.neo4j.Neo4jClient;
import io.zerows.plugins.store.neo4j.Neo4jInfix;

import java.util.function.Function;

abstract class PixelBase implements Pixel {

    protected final transient String identifier;
    protected final transient Neo4jClient client;

    public PixelBase(final String identifier) {
        this.identifier = identifier;
        this.client = Neo4jInfix.getClient();
        if (this.client.connected()) {
            this.client.connect(KWeb.DEPLOY.VERTX_GROUP);
        }
    }

    protected <T> Future<T> runSafe(final T input, final Function<T, Future<T>> executor) {
        return Ox.runSafe(this.getClass(), input, executor);
    }

    protected LogOf logger() {
        return LogOf.get(this.getClass());
    }
}
