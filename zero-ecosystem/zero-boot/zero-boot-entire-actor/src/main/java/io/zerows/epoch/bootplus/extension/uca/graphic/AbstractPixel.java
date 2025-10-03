package io.zerows.epoch.bootplus.extension.uca.graphic;

import io.zerows.epoch.bootplus.extension.refine.Ox;
import io.vertx.core.Future;
import io.zerows.epoch.constant.KWeb;
import io.zerows.component.log.Annal;
import io.zerows.plugins.store.neo4j.Neo4jClient;
import io.zerows.plugins.store.neo4j.Neo4jInfix;

import java.util.function.Function;

abstract class AbstractPixel implements Pixel {

    protected final transient String identifier;
    protected final transient Neo4jClient client;

    public AbstractPixel(final String identifier) {
        this.identifier = identifier;
        this.client = Neo4jInfix.getClient();
        if (this.client.connected()) {
            this.client.connect(KWeb.DEPLOY.VERTX_GROUP);
        }
    }

    protected <T> Future<T> runSafe(final T input, final Function<T, Future<T>> executor) {
        return Ox.runSafe(this.getClass(), input, executor);
    }

    protected Annal logger() {
        return Annal.get(this.getClass());
    }
}
