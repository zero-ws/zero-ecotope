package io.zerows.extension.runtime.graphic.agent.api;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.core.annotations.Address;
import io.zerows.core.annotations.Queue;
import io.zerows.core.constant.KName;
import io.zerows.core.constant.KWeb;
import io.zerows.epoch.common.log.Annal;
import io.zerows.core.util.Ut;
import io.zerows.extension.runtime.graphic.eon.Addr;
import io.zerows.plugins.store.neo4j.Neo4jClient;
import io.zerows.plugins.store.neo4j.Neo4jInfix;
import io.zerows.unity.Ux;

/*
 *
 */
@Queue
public class GraphActor {

    private static final Annal LOGGER = Annal.get(GraphActor.class);

    @Address(Addr.GRAPH_ANALYZE)
    public Future<JsonObject> analyze(final String key, final String graph, final Integer level) {
        final String graphName = Ut.isNil(graph) ? KWeb.DEPLOY.VERTX_GROUP : graph;
        if (Ut.isNil(key)) {
            return Ux.future(new JsonObject());
        } else {
            final Neo4jClient client = Neo4jInfix.getClient();
            LOGGER.info("[ ZERO ] Graphic analyzing for graph = {0}, key = {1}", graphName, key);
            if (client.connected()) {
                return client.connect(graphName).graphicByKey(key, level).compose(graphic -> {
                    final JsonArray nodeRef = graphic.getJsonArray(KName.Graphic.NODES);
                    Ut.valueToJArray(nodeRef, KName.DATA);
                    // Ut.itJArray(nodeRef).forEach(node -> Ke.mount(node, KName.DATA));
                    final JsonArray edgeRef = graphic.getJsonArray(KName.Graphic.EDGES);
                    Ut.valueToJArray(edgeRef, KName.DATA);
                    // Ut.itJArray(edgeRef).forEach(node -> Ke.mount(node, KName.DATA));
                    return Ux.future(graphic);
                });
            } else {
                return Ux.future(new JsonObject()
                    .put(KName.Graphic.NODES, new JsonArray())
                    .put(KName.Graphic.EDGES, new JsonArray())
                );
            }
        }
    }
}
