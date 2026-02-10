package io.zerows.extension.module.rbac.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonObject;
import io.zerows.extension.module.rbac.domain.tables.SPacket;
import io.zerows.extension.module.rbac.domain.tables.SPath;
import io.zerows.extension.module.rbac.domain.tables.SResource;
import io.zerows.extension.module.rbac.domain.tables.SView;
import io.zerows.extension.module.rbac.domain.tables.SVisitant;

import java.util.List;
import java.util.Map;

public class TypeOfRBACJsonObject extends TypeOfJsonObject {
    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // SResource
            Map.of(
                SResource.S_RESOURCE.SEEK_CONFIG.getName(), SResource.S_RESOURCE.getName(),
                SResource.S_RESOURCE.SEEK_SYNTAX.getName(), SResource.S_RESOURCE.getName()
            ),
            // SView
            Map.of(
                SView.S_VIEW.ROWS.getName(), SView.S_VIEW.getName(),
                SView.S_VIEW.CRITERIA.getName(), SView.S_VIEW.getName()
            ),
            // SVisitant
            Map.of(
                SVisitant.S_VISITANT.DM_QR.getName(), SVisitant.S_VISITANT.getName(),
                SVisitant.S_VISITANT.DM_ROW.getName(), SVisitant.S_VISITANT.getName()
            ),
            // SPath
            Map.of(
                SPath.S_PATH.DM_CONDITION.getName(), SPath.S_PATH.getName(),
                SPath.S_PATH.DM_CONFIG.getName(), SPath.S_PATH.getName(),
                SPath.S_PATH.MAPPING.getName(), SPath.S_PATH.getName(),

                SPath.S_PATH.UI_CONDITION.getName(), SPath.S_PATH.getName(),
                SPath.S_PATH.UI_CONFIG.getName(), SPath.S_PATH.getName(),
                SPath.S_PATH.UI_SURFACE.getName(), SPath.S_PATH.getName()
            ),
            // SPacket
            Map.of(
                SPacket.S_PACKET.RUN_CONFIG.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.SEEK_CONFIG.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.SEEK_SYNTAX.getName(), SPacket.S_PACKET.getName(),

                SPacket.S_PACKET.H_CONFIG.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.H_MAPPING.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.Q_CONFIG.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.Q_MAPPING.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.V_CONFIG.getName(), SPacket.S_PACKET.getName(),
                SPacket.S_PACKET.V_MAPPING.getName(), SPacket.S_PACKET.getName()
            )
        );
    }
}
