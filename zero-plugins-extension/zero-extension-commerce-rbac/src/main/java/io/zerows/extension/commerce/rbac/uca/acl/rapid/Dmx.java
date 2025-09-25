package io.zerows.extension.commerce.rbac.uca.acl.rapid;

import io.r2mo.typed.cc.Cc;
import io.vertx.core.json.JsonObject;
import io.zerows.core.util.Ut;
import io.zerows.extension.commerce.rbac.domain.tables.pojos.SVisitant;

/**
 * Quit Normalizer for Matrix ( View )
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface Dmx {

    Cc<String, Dmx> CC_QMX = Cc.openThread();

    static Dmx outlet(final Class<?> clazz) {
        return CC_QMX.pick(() -> Ut.instance(clazz), clazz.getName());
    }

    void output(SVisitant visitant, JsonObject matrixJ);
}
