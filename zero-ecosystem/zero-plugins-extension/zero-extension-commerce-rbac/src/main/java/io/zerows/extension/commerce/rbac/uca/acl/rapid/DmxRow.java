package io.zerows.extension.commerce.rbac.uca.acl.rapid;

import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.program.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SVisitant;
import io.zerows.extension.commerce.rbac.eon.em.QVHMode;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class DmxRow implements Dmx {
    @Override
    public void output(final SVisitant visitant, final JsonObject matrixJ) {
        final JsonObject dmRow = Ut.toJObject(visitant.getDmRow());
        if (Ut.isNil(dmRow)) {
            return;
        }
        final QVHMode mode = Ut.toEnum(visitant::getMode, QVHMode.class, QVHMode.REPLACE);
        // dmRow 只支持两种模式（ REPLACE / EXTEND )
        final JsonObject rowRef = matrixJ.getJsonObject(KName.Rbac.ROWS);
        if (QVHMode.REPLACE == mode || Ut.isNil(rowRef)) {
            // REPLACE 或 View 中没有值
            matrixJ.put(KName.Rbac.ROWS, dmRow);
        }
    }
}
