package io.vertx.boot.lighter;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zerows.extension.runtime.skeleton.boot.lighter.ZeroPre;
import io.zerows.plugins.store.elasticsearch.ElasticSearchInfix;
import io.zerows.plugins.store.neo4j.Neo4jInfix;

/**
 * 在原生的 {@link ZeroPre} 基础之上追加 ES 和 Neo4J 的初始化
 *
 * @author lang : 2023-06-10
 */
public class FullPre extends ZeroPre {
    @Override
    public Boolean beforeStart(final Vertx vertx, final JsonObject options) {
        final Boolean started = super.beforeStart(vertx, options);
        if (!started) {
            return false;
        }
        ElasticSearchInfix.init(vertx);
        Neo4jInfix.init(vertx);
        return Boolean.TRUE;
    }
}
