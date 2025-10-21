package io.zerows.extension.commerce.erp.spi;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.zerows.epoch.constant.KName;
import io.zerows.epoch.store.jooq.DB;
import io.zerows.extension.commerce.erp.domain.tables.daos.EDeptDao;
import io.zerows.extension.skeleton.spi.ExArborBase;
import io.zerows.program.Ux;
import io.zerows.support.Ut;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public class DepartmentArbor extends ExArborBase {
    @Override
    public Future<JsonArray> generate(final JsonObject category, final JsonObject configuration) {
        return DB.on(EDeptDao.class)
            .fetchJAsync(KName.SIGMA, category.getValue(KName.SIGMA))
            .compose(children -> {
                Ut.itJArray(children).forEach(json -> json.put(KName.PARENT_ID, json.getValue("deptId")));
                return Ux.future(children);
            })
            .compose(children -> this.combineArbor(category, children, configuration));
    }
}
