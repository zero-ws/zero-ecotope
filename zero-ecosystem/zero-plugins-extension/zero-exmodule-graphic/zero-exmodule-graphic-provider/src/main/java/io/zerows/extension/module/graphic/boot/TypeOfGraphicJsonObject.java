package io.zerows.extension.module.graphic.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.graphic.domain.tables.GCluster;
import io.zerows.extension.module.graphic.domain.tables.GEdge;
import io.zerows.extension.module.graphic.domain.tables.GGraphic;
import io.zerows.extension.module.graphic.domain.tables.GNode;

import java.util.List;
import java.util.Map;

public class TypeOfGraphicJsonObject extends TypeOfJsonObject {
    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // GCluster
            Map.of(
                GCluster.G_CLUSTER.RECORD_DATA.getName(), GCluster.G_CLUSTER.getName(),
                GCluster.G_CLUSTER.UI.getName(), GCluster.G_CLUSTER.getName()
            ),
            // GEdge
            Map.of(
                GEdge.G_EDGE.RECORD_DATA.getName(), GEdge.G_EDGE.getName(),
                GEdge.G_EDGE.UI.getName(), GEdge.G_EDGE.getName()
            ),
            // GGraphic
            Map.of(
                GGraphic.G_GRAPHIC.UI.getName(), GGraphic.G_GRAPHIC.getName()
            ),
            // GNode
            Map.of(
                GNode.G_NODE.RECORD_DATA.getName(), GNode.G_NODE.getName(),
                GNode.G_NODE.UI.getName(), GNode.G_NODE.getName()
            )
        );
    }
}
