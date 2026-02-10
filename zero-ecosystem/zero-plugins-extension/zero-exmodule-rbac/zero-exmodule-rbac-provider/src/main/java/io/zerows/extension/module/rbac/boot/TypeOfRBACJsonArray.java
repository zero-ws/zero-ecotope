package io.zerows.extension.module.rbac.boot;

import io.r2mo.vertx.jooq.generate.configuration.TypeOfJsonArray;
import io.zerows.extension.module.rbac.domain.tables.SAction;
import io.zerows.extension.module.rbac.domain.tables.SView;
import io.zerows.extension.module.rbac.domain.tables.SVisitant;

import java.util.List;
import java.util.Map;

public class TypeOfRBACJsonArray extends TypeOfJsonArray {
    @Override
    protected List<Map<String, String>> regexMeta() {
        return List.of(
            // SAction
            Map.of(
                SAction.S_ACTION.RENEWAL_CREDIT.getName(), SAction.S_ACTION.getName()
            ),
            // SView
            Map.of(
                SView.S_VIEW.PROJECTION.getName(), SView.S_VIEW.getName()
            ),
            // SVisitant
            Map.of(
                SVisitant.S_VISITANT.ACL_VARIETY.getName(), SVisitant.S_VISITANT.getName(),
                SVisitant.S_VISITANT.ACL_VERGE.getName(), SVisitant.S_VISITANT.getName(),
                SVisitant.S_VISITANT.ACL_VIEW.getName(), SVisitant.S_VISITANT.getName(),
                SVisitant.S_VISITANT.ACL_VISIBLE.getName(), SVisitant.S_VISITANT.getName(),
                SVisitant.S_VISITANT.ACL_VOW.getName(), SVisitant.S_VISITANT.getName(),

                SVisitant.S_VISITANT.DM_COLUMN.getName(), SVisitant.S_VISITANT.getName()
            )
        );
    }
}
